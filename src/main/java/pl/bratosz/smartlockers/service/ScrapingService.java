package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.scraping.Scrapper;

import java.io.IOException;
import java.util.Set;

@Service
public class ScrapingService {
    private PlantService plantService;
    private BoxService boxService;
    private EmployeeService employeeService;
    private ClothService clothService;
    private Scrapper scrapper;
    private Set<Cloth> currentClothes;

    public ScrapingService(
            PlantService plantService, BoxService boxService, EmployeeService employeeService,
            ClothService clothService, Scrapper scrapper) {
        this.plantService = plantService;
        this.boxService = boxService;
        this.employeeService = employeeService;
        this.clothService = clothService;
        this.scrapper = scrapper;
    }

    public Box updateEmployeeClothes(long boxId) throws IOException {
        Box box = boxService.getBoxById(boxId);
        Plant plant = box.getLocker().getPlant();
        Employee employee = (Employee) box.getEmployee();
        currentClothes = employee.getClothes();

        String login = plant.getLogin();
        String password = plant.getPassword();
        scrapper.createConnection(login, password);
        scrapper.findByLockerAndBox(box.getLocker().getLockerNumber(),
                box.getBoxNumber());
        if (employee.getLastName().toUpperCase().equals(
                scrapper.getEmployeeLastName().toUpperCase())) {
            Set<Cloth> actualClothes = scrapper.getClothes();
            clothService.updateClothes(currentClothes, actualClothes, employee);
            return box;
        } else if (employee.getFirstName().toUpperCase().equals(
                scrapper.getEmployeeFirstName().toUpperCase())) {
            Set<Cloth> actualClothes = scrapper.getClothes();
            clothService.updateClothes(currentClothes, actualClothes, employee);
            return boxService.getBoxById(boxId);
        } else {
            throw new IOException("Last names are different!");
        }
    }

    public Box loadEmployee(long departmentId, long boxId) {
        Box box = boxService.getBoxById(boxId);
        int lockerNumber = box.getLocker().getLockerNumber();
        int boxNumber = box.getBoxNumber();

        Plant plant = box.getLocker().getPlant();
        String login = plant.getLogin();
        String password = plant.getPassword();

        scrapper.createConnection(login, password);
        scrapper.findByLockerAndBox(lockerNumber, boxNumber);

        String firstName = scrapper.getEmployeeFirstName();
        String lastName = scrapper.getEmployeeLastName();
        Set<Cloth> clothes = scrapper.getClothes();


        employeeService.createEmployee(
                clothes, departmentId, box, firstName, lastName);
        return boxService.getBoxById(boxId);
    }

}
