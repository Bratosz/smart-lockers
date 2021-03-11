package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.scraping.Scrapper;

import java.io.IOException;
import java.util.List;

@Service
public class ScrapingService {
    private LockerService lockerService;
    private BoxService boxService;
    private EmployeeService employeeService;
    private ClothService clothService;
    private Scrapper scrapper;
    private List<Cloth> currentClothes;

    public ScrapingService(LockerService lockerService,
                           BoxService boxService,
                           EmployeeService employeeService,
                           ClothService clothService,
                           Scrapper scrapper
    ) {
        this.lockerService = lockerService;
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

        scrapper.createConnection(plant);
        scrapper.find(box);
        if (employee.getLastName().equals(
                scrapper.getEmployeeLastName())) {
            List<Cloth> actualClothes = scrapper.getClothes();
            clothService.updateClothes(currentClothes, actualClothes, employee);
            return box;
        } else if (employee.getFirstName().toUpperCase().equals(
                scrapper.getEmployeeFirstName().toUpperCase())) {
            List<Cloth> actualClothes = scrapper.getClothes();
            clothService.updateClothes(currentClothes, actualClothes, employee);
            return boxService.getBoxById(boxId);
        } else {
            throw new IOException("Last names are different!");
        }
    }

    public LockerReport loadLocker(long lockerId) {
        Locker locker = lockerService.getLockerById(lockerId);
        Plant plant = locker.getPlant();

        scrapper.createConnection(plant);
        scrapper.find(locker);

        LockerReport lockerReport = loadBoxes(locker);
        return lockerReport;
    }

    private LockerReport loadBoxes(Locker locker) {

    }


    public Box loadEmployee(long boxId) {
        Box box = boxService.getBoxById(boxId);
        Plant plant = box.getLocker().getPlant();

        scrapper.createConnection(plant);
        scrapper.find(box);
        scrapper.clickViewButton();

        String firstName = scrapper.getEmployeeFirstName();
        String lastName = scrapper.getEmployeeLastName();
        String departmentName = scrapper.getDepartmentName();
        List<Cloth> clothes = scrapper.getClothes();

        employeeService.createEmployee(
                clothes, departmentName, box, firstName, lastName);
        return boxService.getBoxById(boxId);
    }

}
