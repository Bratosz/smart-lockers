package pl.bratosz.smartlockers.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exels.ExcelClothesReader;
import pl.bratosz.smartlockers.exels.LoadedRow;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.ClothesRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static pl.bratosz.smartlockers.model.ClothType.EMPLOYEE;
import static pl.bratosz.smartlockers.model.ClothType.ROTATIONAL;

@Service
public class ClothesService {
    private EmployeeService employeeService;
    private ClothesRepository clothesRepository;

    @Autowired
    public ClothesService(EmployeeService employeeService, ClothesRepository clothesRepository) {
        this.employeeService = employeeService;
        this.clothesRepository = clothesRepository;

    }

    public Set<Cloth> update(Sheet sheet) {
        ExcelClothesReader clothesReader = ExcelClothesReader.create(sheet);
        List<LoadedRow> loadedRows = clothesReader.loadRows();
        Set<Cloth> clothes = loadClothes(loadedRows);
        return clothes;

    }

    private Set<Cloth> loadClothes(List<LoadedRow> loadedRows) {
        Set<Cloth> clothes = new HashSet<>();
        for(LoadedRow row : loadedRows) {
            Cloth cloth = createCloth(row);
            clothes.add(cloth);
            clothesRepository.save(cloth);
        }
        return clothes;
    }

    private Cloth createCloth(LoadedRow loadedRow) {
        ClothType clothType = resolveClothType(loadedRow.getLockerNo());
        if(clothType.equals(EMPLOYEE)) {
            Employee employee = employeeService.getEmployeeByFullNameAndFullBoxNumber(
                    loadedRow.getFirstName(),
                    loadedRow.getLastName(),
                    loadedRow.getLockerNo(),
                    loadedRow.getBoxNo()
            );
            return new EmployeeCloth(
                    loadedRow.getBarCode(),
                    loadedRow.getWashingDate(),
                    loadedRow.getOrdinalNo(),
                    loadedRow.getArticleNo(),
                    employee
            );
        } else {
            return new RotationalCloth(
                    loadedRow.getBarCode(),
                    loadedRow.getWashingDate(),
                    loadedRow.getOrdinalNo(),
                    loadedRow.getArticleNo()
            );
        }
    }

    private ClothType resolveClothType(int lockerNo) {
        if(lockerNo == 0) {
            return ROTATIONAL;
        } else {
            return EMPLOYEE;
        }
    }

    public Cloth getClothById(long id) {
        return clothesRepository.getClothById(id);
    }
}
