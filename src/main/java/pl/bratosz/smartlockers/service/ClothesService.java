package pl.bratosz.smartlockers.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.repository.RotationalClothesRepository;
import pl.bratosz.smartlockers.service.exels.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.ClothesRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static pl.bratosz.smartlockers.service.exels.ClothOperationType.*;

@Service
public class ClothesService {
    private EmployeeService employeeService;
    private ClothesRepository clothesRepository;
    private RotationalClothesRepository rotationalClothesRepository;

    @Autowired
    public ClothesService(EmployeeService employeeService, ClothesRepository clothesRepository,
                          RotationalClothesRepository rotationalClothesRepository) {
        this.employeeService = employeeService;
        this.clothesRepository = clothesRepository;
        this.rotationalClothesRepository = rotationalClothesRepository;

    }

    public List<Cloth> uploadClothesRotation(Sheet sheet, ClothOperationType clothOperationType) {
        ExcelClothesReader clothesReader = ExcelClothesReader.create(sheet, clothOperationType);
        List<RowForRotationUpdate> rowsForRotationUpdate = clothesReader.loadRows();
        return loadClothesRotation(rowsForRotationUpdate);
    }

    public List<RotationalCloth> updateReleasedRotation(Sheet sheet, ClothOperationType clothOperationType) {
        if (clothOperationType.equals(RELEASED_ROTATIONAL_CLOTHING_UPDATE)) {
            ExcelClothesReader clothesReader = ExcelClothesReader.create(sheet, clothOperationType);
            List<RowReleasedRotationalClothes> loadedRotationalClothing = clothesReader.loadRows();
            return updateClothesRotation(loadedRotationalClothing);
        }
        return null;
    }

    private List<RotationalCloth> updateClothesRotation(List<RowReleasedRotationalClothes> clothRows) {
        List<RotationalCloth> clothes = new LinkedList<>();
        List<RotationalCloth> all = rotationalClothesRepository.findAll();
        for (RowReleasedRotationalClothes row : clothRows) {
            if (all.stream().anyMatch(r -> r.getId() == row.getBarCode())) {
                RotationalCloth cloth = rotationalClothesRepository.getOne(row.getBarCode());
                Employee employee = employeeService.getOneEmployee(row.getFirstName(), row.getLastName());
                if (employee.getId() == 1) {
                    continue;
                }
                cloth.setReleasedToEmployee(row.getReleaseDate());
                cloth.setEmployee(employee);
                clothes.add(cloth);
            }
        }
        return clothesRepository.saveAll(clothes);
    }


    private List<Cloth> loadClothesRotation(List<RowForRotationUpdate> rowForRotationUpdate) {
        List<Cloth> clothes = new LinkedList<>();
        for (RowForRotationUpdate row : rowForRotationUpdate) {
            Cloth cloth = createCloth(row);
            clothes.add(cloth);
        }
        return clothesRepository.saveAll(clothes);

    }

    private Cloth createCloth(RowForRotationUpdate rowForRotationUpdate) {

        if (isClothRotational(rowForRotationUpdate.getLockerNo())) {

            return new RotationalCloth();
        } else {
            Employee employee = employeeService.getEmployeeByFullNameAndFullBoxNumber(
                    rowForRotationUpdate.getFirstName(),
                    rowForRotationUpdate.getLastName(),
                    rowForRotationUpdate.getLockerNo(),
                    rowForRotationUpdate.getBoxNo()
            );
            return new EmployeeCloth();
        }
    }

    private boolean isClothRotational(int lockerNo) {
        if (lockerNo == 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cloth getClothById(long id) {
        return clothesRepository.getClothById(id);
    }

    public List<RotationalCloth> getRotationalClothRaport() throws IOException {
        List<RotationalCloth> rotationalClothes = rotationalClothesRepository.getReleasedRotationalClothes();
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Rotacja do zwrócenia");
        Row row;
        for (int i = 0; i < rotationalClothes.size(); i++) {
            row = sheet.createRow(i);
            RotationalCloth cloth = rotationalClothes.get(i);
            Employee emp = cloth.getEmployee();
            row.createCell(0).setCellValue(emp.getId());
            row.createCell(1).setCellValue(emp.getFirstName());
            row.createCell(2).setCellValue(emp.getLastName());
            row.createCell(3).setCellValue(emp.getFirstLockerNumber());
            row.createCell(4).setCellValue(emp.getFirstBoxNumber());
            row.createCell(5).setCellValue(emp.getDepartment().getName());
            row.createCell(6).setCellValue(cloth.getId());
            row.createCell(7).setCellValue(cloth.getArticle().getName());
            row.createCell(8).setCellValue(cloth.getArticle().getId());
        }
        FileOutputStream fileOut = new FileOutputStream("C:/Users/HP/Desktop/files_to_testing/Lear/raports/rotacja_do_zwrotu.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
        return rotationalClothes;
    }
}
