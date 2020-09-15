package pl.bratosz.smartlockers.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.service.exels.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.ClothesRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static pl.bratosz.smartlockers.service.exels.ClothOperationType.*;

@Service
public class ClothService {
    private EmployeeService employeeService;
    private ClothesRepository clothesRepository;

    public ClothService(EmployeeService employeeService, ClothesRepository clothesRepository) {
        this.employeeService = employeeService;
        this.clothesRepository = clothesRepository;
    }

    public List<Cloth> uploadClothesRotation(Sheet sheet, ClothOperationType clothOperationType) {
        ExcelClothesReader clothesReader = ExcelClothesReader.create(sheet, clothOperationType);
        List<RowForRotationUpdate> rowsForRotationUpdate = clothesReader.loadRows();
        return loadClothesRotation(rowsForRotationUpdate);
    }

    public List<Cloth> updateReleasedRotation(Sheet sheet, ClothOperationType clothOperationType) {
        if (clothOperationType.equals(RELEASED_ROTATIONAL_CLOTHING_UPDATE)) {
            ExcelClothesReader clothesReader = ExcelClothesReader.create(sheet, clothOperationType);
            List<RowReleasedRotationalClothes> loadedRotationalClothing = clothesReader.loadRows();
            return updateClothesRotation(loadedRotationalClothing);
        }
        return null;
    }

    private List<Cloth> updateClothesRotation(List<RowReleasedRotationalClothes> clothRows) {
        List<Cloth> clothes = new LinkedList<>();
        List<Cloth> all = findAllClothesByRotationalValue(true);
        for (RowReleasedRotationalClothes row : clothRows) {
            if (all.stream().anyMatch(r -> r.getId() == row.getBarCode())) {
                Cloth cloth = clothesRepository.getOne(row.getBarCode());
                Employee employee = employeeService.getOneEmployee(row.getFirstName(), row.getLastName());
                if (employee.getId() == 1) {
                    continue;
                }
                cloth.setReleaseAsRotationalDate(row.getReleaseDate());
                cloth.setRotationOwner(employee);
                clothes.add(cloth);
            }
        }
        return clothesRepository.saveAll(clothes);
    }

    private List<Cloth> findAllClothesByRotationalValue(boolean isRotational) {
        List<Cloth> clothes = clothesRepository.findAllClothesByRotationalValue(isRotational);
        return clothes;
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
            return new Cloth();
        } else {
            Employee employee = employeeService.getEmployeeByFullNameAndFullBoxNumber(
                    rowForRotationUpdate.getFirstName(),
                    rowForRotationUpdate.getLastName(),
                    rowForRotationUpdate.getLockerNo(),
                    rowForRotationUpdate.getBoxNo()
            );
            return new Cloth();
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
        return clothesRepository.getOne(id);
    }

    public List<Cloth> getRotationalClothRaport() throws IOException {
        List<Cloth> rotationalClothes = getReleasedRotationalClothes();
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Rotacja do zwrócenia");
        Row row;
        for (int i = 0; i < rotationalClothes.size(); i++) {
            row = sheet.createRow(i);
            Cloth cloth = rotationalClothes.get(i);
            Employee emp = cloth.getEmployee();
            row.createCell(0).setCellValue(emp.getId());
            row.createCell(1).setCellValue(emp.getFirstName());
            row.createCell(2).setCellValue(emp.getLastName());
            row.createCell(3).setCellValue(emp.getFirstLockerNumber());
            row.createCell(4).setCellValue(emp.getFirstBoxNumber());
            row.createCell(5).setCellValue(emp.getDepartment().getName());
            row.createCell(6).setCellValue(cloth.getId());
            row.createCell(7).setCellValue(cloth.getArticle().getName());
            row.createCell(8).setCellValue(cloth.getArticle().getArticleNumber());
        }
        FileOutputStream fileOut = new FileOutputStream("C:/Users/HP/Desktop/files_to_testing/Lear/raports/rotacja_do_zwrotu.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
        return rotationalClothes;
    }

    private List<Cloth> getReleasedRotationalClothes() {
        return clothesRepository.getReleasedRotationalClothes();
    }

    public List<Cloth> getClothesByIds(long[] clothsIds) {
        List<Cloth> clothes = new LinkedList<>();
        for(int i = 0; i < clothsIds.length; i++) {
            long clothId = clothsIds[i];
            Cloth cloth = clothesRepository.getClothById(clothId);
            clothes.add(cloth);
        }
        return clothes;
    }

    public ClothSize determineDesiredSize(ClothSize desiredSize, ClothSize actualSize) {
        if(desiredSize == ClothSize.SIZE_DEFAULT) {
            return actualSize;
        } else {
            return desiredSize;
        }
    }
}
