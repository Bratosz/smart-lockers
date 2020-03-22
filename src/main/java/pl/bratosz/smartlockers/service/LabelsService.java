package pl.bratosz.smartlockers.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.service.exels.ExcelSave;
import pl.bratosz.smartlockers.service.exels.ExcelWriter;
import pl.bratosz.smartlockers.service.exels.LabelsSheetParameters;
import pl.bratosz.smartlockers.formaters.StringFormater;
import pl.bratosz.smartlockers.model.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class LabelsService {
    private BoxesService boxesService;

    public LabelsService(BoxesService boxesService) {
        this.boxesService = boxesService;
    }

    public void prepareLabelsAndSave(String folderName, Locker.DepartmentNumber depNumber, int firstLocker, int lastLocker) throws IOException {
        List<Box> boxes = boxesService.getBoxesByLockersRange(depNumber, firstLocker, lastLocker);
        List<String> labels = createLabelsFromBoxes(boxes);
        createLabelsSpreadSheetAndSave(folderName, labels);
    }

    public String prepareLabelsAndSave(String folderName, List<LabelEmployee> employees) throws IOException {
        List<String> labels = createLabelsFromRawEmployees(employees);
        String filename = createLabelsSpreadSheetAndSave(folderName, labels);
        return filename;
    }

    private String createLabelsSpreadSheetAndSave(String folderName, List<String> labels) throws IOException {
        LabelsSheetParameters parameters = new LabelsSheetParameters();
        ExcelWriter writer = new ExcelWriter(parameters);
        XSSFWorkbook labelsSS = writer.createLabels(labels);

        ExcelSave excelSave = new ExcelSave();
        String fileName = excelSave.save(labelsSS, folderName);
        return  fileName;
    }

    private List<String> createLabelsFromBoxes(List<Box> boxes) {
        List<String> labels = new LinkedList<>();
        for (Box box : boxes) {
            if (box.getBoxStatus() == Box.BoxStatus.OCCUPY) {
                labels.add(getLabelFromBox(box));
            }
        }
        return labels;
    }

    private List<String> createLabelsFromRawEmployees(List<LabelEmployee> employees) {
        List<String> labels = new LinkedList<>();
        for (LabelEmployee emp : employees) {
            labels.add(getLabelFromRawEmployee(emp));
        }
        return labels;
    }

    private String getLabelFromRawEmployee(LabelEmployee emp) {
        String firstName = emp.getFirstName();
        String lastName = emp.getLastName();
        int lockerNumber = emp.getLockerNumber();
        int boxNumber = emp.getBoxNumber();
        String label = createLabel(firstName, lastName, lockerNumber, boxNumber);

        return label;
    }

    private String getLabelFromBox(Box box) {
        Employee emp = box.getEmployee();
        String firstName = emp.getFirstName();
        String lastName = emp.getLastName();
        int lockerNumber = box.getLocker().getLockerNumber();
        int boxNumber = box.getBoxNumber();

        return createLabel(firstName, lastName, lockerNumber, boxNumber);
    }

    private String createLabel(String firstName, String lastName, int lockerNumber, int boxNumber) {
        String fullBoxNumber = lockerNumber + "/" + boxNumber;
        StringFormater sf = new StringFormater();

        return "\n"
                + sf.capitalizeFirstLetter(lastName) + " " + sf.capitalizeFirstLetter(firstName)
                + "\n"
                + "                               " + fullBoxNumber;
    }
}
