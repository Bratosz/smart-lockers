package pl.bratosz.smartlockers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.repository.ClothesRepository;
import pl.bratosz.smartlockers.scraping.ServiceScrapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

@Service
public class ScrapingService {
    private BoxesRepository boxesRepository;
    private ClothesRepository clothesRepository;
    private Set<EmployeeCloth> currentClothes;

    @Autowired
    public ScrapingService(BoxesRepository boxesRepository, ClothesRepository clothesRepository) {
        this.boxesRepository = boxesRepository;
        this.clothesRepository = clothesRepository;
    }


    public Box updateEmployeeClothes(long boxId) throws IOException {
        Box box = boxesRepository.getBoxById(boxId);
        Locker.DepartmentNumber departmentNumber = box.getLocker().getDepartmentNumber();
        Employee employee = box.getEmployee();
        currentClothes = employee.getClothing();
        ServiceScrapper scrapper = new ServiceScrapper(departmentNumber);
        scrapper.findByLockerAndBox(box.getLocker().getLockerNumber(),
                                    box.getBoxNumber());
        if(employee.getLastName().equals(
                scrapper.getEmployeeLastName().toUpperCase())) {
            Set<EmployeeCloth> actualClothes = scrapper.getClothes();
            compareAndUpdateClothes(actualClothes, employee);
            return box;
        } else {
            throw new IOException("Last names are different!");
        }
    }

    private void compareAndUpdateClothes(Set<EmployeeCloth> actualClothes, Employee employee) {
        Set<EmployeeCloth> newClothes = new HashSet<>();
        if(currentClothes.isEmpty()) {
            for(EmployeeCloth cloth : actualClothes){
                cloth.setEmployee(employee);
                newClothes.add(cloth);
            }
        } else {
            for(EmployeeCloth cloth : currentClothes) {
                if(!actualClothes.contains(cloth)) {
                    cloth.setActive(false);
                    continue;
                }
                for(EmployeeCloth actualCloth : actualClothes) {
                    if(!currentClothes.contains(actualCloth)) {
                        actualCloth.setEmployee(employee);
                        newClothes.add(actualCloth);
                    } else if(actualCloth.equals(cloth)) {
                        cloth.setLastWashing(actualCloth.getLastWashing());
                    }
                }
                clothesRepository.flush();
            }
        }
        clothesRepository.saveAll(newClothes);
    }
}
