package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.BoxNotAvailableException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.response.StandardResponse;
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
    private LocationService locationService;
    private DepartmentService departmentService;
    private Scrapper scrapper;
    private List<Cloth> currentClothes;
    private LockersLoadReport lockersLoadReport;
    private User user;

    public ScrapingService(LockerService lockerService,
                           BoxService boxService,
                           EmployeeService employeeService,
                           ClothService clothService,
                           PlantService plantService, UserService userService, LocationService locationService, DepartmentService departmentService, Scrapper scrapper
    ) {
        this.lockerService = lockerService;
        this.boxService = boxService;
        this.employeeService = employeeService;
        this.clothService = clothService;
        this.plantService = plantService;
        this.userService = userService;
        this.locationService = locationService;
        this.departmentService = departmentService;
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
        Client client = plant.getClient();
        Employee employee = (Employee) box.getEmployee();
        currentClothes = employee.getClothes();

        scrapper.createConnection(plant);
        scrapper.goToLockersView();
        scrapper.find(box);
        scrapper.clickViewButton();
        String lastName = scrapper.getEmployeeLastName();
        if (employee.getLastName().equals(lastName)
        || employee.getFirstName().equals(lastName)) {
            List<Cloth> actualClothes = scrapper.getClothes(client);
            clothService.updateClothes(currentClothes, actualClothes, employee, user);
            return box;
        } else {
            throw new IOException("Last names are different!");
        }
    }

    public LockersLoadReport loadLocker(long lockerId) {
        Locker locker = lockerService.getBy(lockerId);
        Plant plant = locker.getPlant();
        lockersLoadReport = new LockersLoadReport(plant.getPlantNumber());

        scrapper.createConnection(plant);
        scrapper.goToLockersView();
        scrapper.find(locker);
        loadBoxesWithClothes(locker);

        return lockersLoadReport;
    }

    private Locker loadLocker(int lockerNumber,
                              int defaultCapacity,
                              Location location,
                              Department department,
                              Plant plant) {
        if (lockerNumber == 0) {
            List<Integer> boxNumbers = scrapper.getBoxNumbers();
            return lockerService.createWithCustomBoxNumbers(lockerNumber, boxNumbers, location, department, plant);
        } else {
            return lockerService.create(lockerNumber, defaultCapacity, plant, department, location);
        }
    }

    private void loadBoxesWithClothes(Locker locker) {
        loadBoxes(locker, true);
    }

    private void loadBoxes(Locker locker, boolean withClothes) {
        List<Integer> boxNumbers = scrapper.getBoxNumbers();
        List<Integer> duplicates = Utils.getDuplicates(boxNumbers);
        for (int i = 1; i <= boxNumbers.size(); i++) {
            int boxNumber = scrapper.getBoxNumberByTableRow(i);
            if (boxIsDuplicated(boxNumber, duplicates)) {
                Box additionalBox = boxService.createAdditionalDuplicatedBox(boxNumber, locker);
                loadEmployee(i, additionalBox, withClothes);
            } else {
                try {
                    Box box = lockerService.getBoxByNumber(locker, boxNumber);
                    loadEmployee(i, box, withClothes);
                } catch (BoxNotAvailableException e) {
                }
            }
        }
    }

    private boolean boxIsDuplicated(int boxNumber, List<Integer> duplicates) {
        if (duplicates.contains(boxNumber)) {
            return true;
        } else {
            return false;
        }
    }

    private void loadEmployee(int rowIndex, Box box, boolean withClothes) {
        String firstName = scrapper.getEmployeeFirstName(rowIndex);
        String lastName = scrapper.getEmployeeLastName(rowIndex);
        String departmentName = scrapper.getDepartmentName(rowIndex);
        Client client = box.getLocker().getPlant().getClient();

        if(withClothes) {
            scrapper.clickViewButtonByRowIndex(rowIndex);
            List<Cloth> clothes = scrapper.getClothes(client);
            clothes = clothService.createExisting(clothes);
            employeeService.createEmployee(
                    clothes, departmentName, box, firstName, lastName);
        } else {
            employeeService.create(firstName, lastName, box, departmentName);
        }
    }

    public Box loadEmployee(long boxId) {
        Box box = boxService.getBoxById(boxId);
        Plant plant = box.getLocker().getPlant();
        Client client = plant.getClient();

        scrapper.createConnection(plant);
        scrapper.goToLockersView();
        scrapper.find(box);
        scrapper.clickViewButton();

        List<Cloth> clothes = scrapper.getClothes(client);
        User user = userService.getDefaultUser();
        clothes = clothService.createExisting(clothes);

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
        scrapper.goToLockersView();

        Set<Locker> lockers = plant.getLockers();
        lockers.stream().forEach(
                l -> loadBoxesWithClothes(l));
        return lockersLoadReport;
    }

    public StandardResponse loadLockers(
            long plantId,
            int firstLockerNumber,
            int lastLockerNumber,
            int capacity,
            boolean withClothes) {
        Plant plant = plantService.getById(plantId);
        Client client = plant.getClient();

        scrapper.createConnection(plant);
        scrapper.goToLockersView();

        Location location = locationService.getSurrogateBy(client);
        Department department = departmentService.getSurrogateBy(client);
        for (int i = firstLockerNumber; i <= lastLockerNumber; i++) {
            if (lockerExistThenGoToIt(i)) {
                Locker locker = loadLocker(i, capacity, location, department, plant);
                loadBoxes(locker, withClothes);
            } else {
                continue;
            }
        }
        return new StandardResponse("Udało się", true);
    }

    private boolean lockerExistThenGoToIt(int lockerNumber) {
        scrapper.findLocker(lockerNumber);
        if (scrapper.getBoxNumbers().isEmpty()) {
            return false;
        }
        return true;
    }


}
