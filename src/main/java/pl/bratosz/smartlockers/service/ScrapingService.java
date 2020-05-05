package pl.bratosz.smartlockers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.repository.ClothesRepository;
import pl.bratosz.smartlockers.scraping.ServiceScrapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

@Service
public class ScrapingService {
    private BoxesRepository boxesRepository;
    private ClothesRepository clothesRepository;

    @Autowired
    public ScrapingService(BoxesRepository boxesRepository, ClothesRepository clothesRepository) {
        this.boxesRepository = boxesRepository;
        this.clothesRepository = clothesRepository;
    }


    public Box updateEmployeeClothes(long boxId) throws IOException {
        Box box = boxesRepository.getBoxById(boxId);
        Locker.DepartmentNumber departmentNumber = box.getLocker().getDepartmentNumber();
        Employee employee = box.getEmployee();
        Set<EmployeeCloth> currentClothes = employee.getClothing();
        ServiceScrapper scrapper = new ServiceScrapper(departmentNumber);
        scrapper.findByLockerAndBox(box.getLocker().getLockerNumber(),
                                    box.getBoxNumber());
        if(employee.getLastName().equals(
                scrapper.getEmployeeLastName().toUpperCase())) {
            Set<EmployeeCloth> actualClothes = scrapper.getClothes();
            Set<EmployeeCloth> employeeNewClothes =
                    compareAndUpdateClothes(currentClothes, actualClothes);
                for(EmployeeCloth cloth : employeeNewClothes) {
                    cloth.setEmployee(employee);
                    clothesRepository.save(cloth);
                }
                return box;
        } else {
            throw new IOException("Last names are different!");
        }
    }

    private Set<EmployeeCloth> compareAndUpdateClothes(
            Set<EmployeeCloth> currentClothes, Set<EmployeeCloth> actualClothes) {
        if(currentClothes.isEmpty()) {
            return actualClothes;
        } else {
            for(EmployeeCloth cloth : currentClothes) {
                boolean isFound = false;
                EmployeeCloth clothToDelete = null;
                for (Iterator<EmployeeCloth> it = actualClothes.iterator(); it.hasNext(); ) {
                  EmployeeCloth  actualCloth = it.next();
                    if (actualCloth.equals(cloth)) {
                        clothToDelete = actualCloth;
                        cloth.setLastWashing(actualCloth.getLastWashing());
                        isFound = true;
                    }
                }
                if (isFound) {
                    actualClothes.remove(clothToDelete);
                } else {
                    clothesRepository.delete(cloth);
                }
            }
            clothesRepository.saveAll(currentClothes);
            return actualClothes;
        }
    }
}
