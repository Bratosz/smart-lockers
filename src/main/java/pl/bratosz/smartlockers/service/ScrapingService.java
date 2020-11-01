package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.repository.ClothesRepository;
import pl.bratosz.smartlockers.scraping.Scrapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class ScrapingService {
    private BoxesRepository boxesRepository;
    private ClothesRepository clothesRepository;
    private Set<Cloth> currentClothes;
    private PlantService plantService;
    private Scrapper scrapper;

    public ScrapingService(BoxesRepository boxesRepository, ClothesRepository clothesRepository,
                           PlantService plantService, Scrapper scrapper) {
        this.boxesRepository = boxesRepository;
        this.clothesRepository = clothesRepository;
        this.plantService = plantService;
        this.scrapper = scrapper;
    }

    public Box updateEmployeeClothes(long boxId) throws IOException {
        Box box = boxesRepository.getBoxById(boxId);
        Plant plant = box.getLocker().getPlant();
        Employee employee = (Employee) box.getEmployee();
        currentClothes = employee.getClothing();

        String login = plant.getLogin();
        String password = plant.getPassword();
        scrapper.createConnection(login, password);
        scrapper.findByLockerAndBox(box.getLocker().getLockerNumber(),
                box.getBoxNumber());
        if (employee.getLastName().toUpperCase().equals(
                scrapper.getEmployeeLastName().toUpperCase())) {
            Set<Cloth> actualClothes = scrapper.getClothes();
            compareAndUpdateClothes(actualClothes, employee);
            return box;
        } else if (employee.getFirstName().toUpperCase().equals(
                scrapper.getEmployeeFirstName().toUpperCase())) {
            Set<Cloth> actualClothes = scrapper.getClothes();
            compareAndUpdateClothes(actualClothes, employee);
            return box;
        } else{
            throw new IOException("Last names are different!");
        }
    }

    private void compareAndUpdateClothes(Set<Cloth> actualClothes, Employee employee) {
        Set<Cloth> newClothes = new HashSet<>();
        if (currentClothes.isEmpty()) {
            for (Cloth cloth : actualClothes) {
                cloth.setEmployee(employee);
                newClothes.add(cloth);
            }
        } else {
            for (Cloth cloth : currentClothes) {
                if (!actualClothes.contains(cloth)) {
                    cloth.setActive(false);
                    continue;
                }
                for (Cloth actualCloth : actualClothes) {
                    if (!currentClothes.contains(actualCloth)) {
                        actualCloth.setEmployee(employee);
                        newClothes.add(actualCloth);
                    } else if (actualCloth.equals(cloth)) {
                        cloth.setLastWashing(actualCloth.getLastWashing());
                    }
                }
                clothesRepository.flush();
            }
        }
        clothesRepository.saveAll(newClothes);
    }
}
