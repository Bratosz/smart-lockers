package pl.bratosz.smartlockers.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exels.ExcelSave;
import pl.bratosz.smartlockers.exels.ExcelWriter;
import pl.bratosz.smartlockers.exels.LabelsSheetParameters;
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

    public void prepareLabelsAndSave(String folderName, String sheetName, Locker.DepartmentNumber depNumber, int firstLocker, int lastLocker) throws IOException {
        List<Box> boxes = boxesService.getBoxesByLockersRange(depNumber, firstLocker, lastLocker);
        List<String> labels = createLabelsFromBoxes(boxes);
        createLabelsSpreadSheetAndSave(folderName, sheetName, labels);
    }

    public void prepareLabelsAndSave(String folderName, String sheetName, List<LabelEmployee> employees) throws IOException {
        List<String> labels = createLabelsFromRawEmployees(employees);
        createLabelsSpreadSheetAndSave(folderName, sheetName, labels);
    }

    private void createLabelsSpreadSheetAndSave(String folderName, String sheetName, List<String> labels) throws IOException {
        LabelsSheetParameters parameters = new LabelsSheetParameters();
        ExcelWriter writer = new ExcelWriter(parameters);
        XSSFWorkbook labelsSS = writer.createLabels(labels);
        ExcelSave excelSave = new ExcelSave();
        excelSave.save(labelsSS, folderName);
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

    private String getLabelFromBox(Box box) {
        Employee emp = box.getEmployee();
        String firstName = emp.getFirstName();
        String lastName = emp.getLastName();
        int lockerNumber = box.getLocker().getLockerNumber();
        int boxNumber = box.getBoxNumber();

        return createLabel(firstName, lastName, lockerNumber, boxNumber);
    }

    private String getLabelFromRawEmployee(LabelEmployee emp) {
        String firstName = emp.getFirstName();
        String lastName = emp.getLastName();
        int lockerNumber = emp.getLockerNumber();
        int boxNumber = emp.getBoxNumber();
        String label = createLabel(firstName, lastName, lockerNumber, boxNumber);

        return label;
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
