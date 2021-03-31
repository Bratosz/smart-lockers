package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.BoxNotAvailableException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.scraping.Scrapper;
import pl.bratosz.smartlockers.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class ScrapingService {
    private LockerService lockerService;
    private BoxService boxService;
    private EmployeeService employeeService;
    private ClothService clothService;
    private PlantService plantService;
    private UserService userService;
    private Scrapper scrapper;
    private List<Cloth> currentClothes;
    private LockersLoadReport lockersLoadReport;
    private User user;

    public ScrapingService(LockerService lockerService,
                           BoxService boxService,
                           EmployeeService employeeService,
                           ClothService clothService,
                           PlantService plantService, UserService userService, Scrapper scrapper
    ) {
        this.lockerService = lockerService;
        this.boxService = boxService;
        this.employeeService = employeeService;
        this.clothService = clothService;
        this.plantService = plantService;
        this.userService = userService;
        this.scrapper = scrapper;
    }

    private void loadUser(long userId) {
        User user = userService.getUserById(userId);
        this.user = user;
    }

    public Box updateEmployeeClothes(
            long boxId,
            long userId) throws IOException {
        loadUser(userId);
        Box box = boxService.getBoxById(boxId);
        Plant plant = box.getLocker().getPlant();
        Employee employee = (Employee) box.getEmployee();
        currentClothes = employee.getClothes();

        scrapper.createConnection(plant);
        scrapper.find(box);
        if (employee.getLastName().equals(
                scrapper.getEmployeeLastName())) {
            List<Cloth> actualClothes = scrapper.getClothes();
            clothService.updateClothes(currentClothes, actualClothes, employee, user);
            return box;
        } else if (employee.getFirstName().toUpperCase().equals(
                scrapper.getEmployeeFirstName().toUpperCase())) {
            List<Cloth> actualClothes = scrapper.getClothes();
            clothService.updateClothes(currentClothes, actualClothes, employee, user);
            return boxService.getBoxById(boxId);
        } else {
            throw new IOException("Last names are different!");
        }
    }

    public LockersLoadReport loadLocker(long lockerId) {
        Locker locker = lockerService.getLockerById(lockerId);
        Plant plant = locker.getPlant();
        lockersLoadReport = new LockersLoadReport(plant.getPlantNumber());

        scrapper.createConnection(plant);
        scrapper.find(locker);
        loadBoxes(locker);

        return lockersLoadReport;
    }

    private void loadLocker(Locker locker) {
        scrapper.find(locker);
        loadBoxes(locker);
    }

    private void loadBoxes(Locker locker) {
        List<Integer> boxNumbers = scrapper.getBoxNumbers();
        List<Integer> duplicates = Utils.getDuplicates(boxNumbers);
        for(int i = 1; i <= boxNumbers.size(); i++) {
                int boxNumber = scrapper.getBoxNumberByTableRow(i);
                if(boxIsDuplicated(boxNumber, duplicates)) {
                    BoxInfo boxInfo = new BoxInfo(locker.getLockerNumber(), boxNumber);
                    lockersLoadReport.getDuplicatedBoxes().add(boxInfo);
                } else {
                    try {
                        Box box = lockerService.getBoxByNumber(locker, boxNumber);
                        loadEmployee(i, box);
                    } catch (BoxNotAvailableException e) {
                    }
                }
            }
    }

    private boolean boxIsDuplicated(int boxNumber, List<Integer> duplicates) {
        if(duplicates.contains(boxNumber)) {
            return true;
        } else {
            return false;
        }
    }

    private void loadEmployee(int rowIndex, Box box) {
        String firstName = scrapper.getEmployeeFirstName(rowIndex);
        String lastName = scrapper.getEmployeeLastName(rowIndex);
        String departmentName = scrapper.getDepartmentName(rowIndex);

        scrapper.clickViewButtonByRowIndex(rowIndex);
        List<Cloth> clothes = scrapper.getClothes();
        User user = userService.getDefaultUser();
        clothes = clothService.createExisting(clothes, user);
        employeeService.createEmployee(
                clothes, departmentName, box, firstName, lastName);
    }

    public Box loadEmployee(long boxId) {
        Box box = boxService.getBoxById(boxId);
        Plant plant = box.getLocker().getPlant();

        scrapper.createConnection(plant);
        scrapper.find(box);
        scrapper.clickViewButton();

        List<Cloth> clothes = scrapper.getClothes();
        User user = userService.getDefaultUser();
        clothes = clothService.createExisting(clothes, user);

        String firstName = scrapper.getEmployeeFirstName();
        String lastName = scrapper.getEmployeeLastName();
        String departmentName = scrapper.getDepartmentName();
        employeeService.createEmployee(
                clothes, departmentName, box, firstName, lastName);
        return boxService.getBoxById(box.getId());
    }

    public LockersLoadReport loadPlantBoxByBox(long plantId) {
        Plant plant = plantService.getById(plantId);
        lockersLoadReport = new LockersLoadReport(plant.getPlantNumber());

        scrapper.createConnection(plant);

        Set<Locker> lockers = plant.getLockers();
        lockers.stream().forEach(
                l -> loadLocker(l));
        return lockersLoadReport;
    }
}
