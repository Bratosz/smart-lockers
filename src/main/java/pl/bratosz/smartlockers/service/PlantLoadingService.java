package pl.bratosz.smartlockers.service;

import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.DoubledBoxException;
import pl.bratosz.smartlockers.exception.EmptyElementException;
import pl.bratosz.smartlockers.exception.SkippedEmployeeException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.response.ClothesLoadedResponse;
import pl.bratosz.smartlockers.response.StandardResponse;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
public class PlantLoadingService {
    private PlantService plantService;
    private LockerService lockerService;
    private BoxService boxService;
    private EmployeeService employeeService;
    private ClothService clothService;
    private ScrapingService scrapingService;
    private UserService userService;

    public PlantLoadingService(PlantService plantService,
                               LockerService lockerService,
                               BoxService boxService,
                               EmployeeService employeeService,
                               ClothService clothService, ScrapingService scrapingService, UserService userService) {
        this.plantService = plantService;
        this.lockerService = lockerService;
        this.boxService = boxService;
        this.employeeService = employeeService;
        this.clothService = clothService;
        this.scrapingService = scrapingService;
        this.userService = userService;
    }

    public StandardResponse loadLockersWithBoxesAndEmployees(long plantId) {
        Plant plant = plantService.getById(plantId);
        scrapingService.connectToBoxesView(plant);
        List<Element> boxesRows = scrapingService.getSortedBoxesRows();
        Client client = plant.getClient();
        long clientId = client.getId();
        Location location = client.getLocations()
                .stream().filter(Location::isSurrogate).findFirst().get();
        Department department = client.getDepartments()
                .stream().filter(Department::isSurrogate).findFirst().get();
        int numberOfLockers = createEmployeesBoxesAndLockers(
                boxesRows,
                plant,
                department,
                location,
                clientId);
        return new StandardResponse(
                "Utworzono " + numberOfLockers + " szaf.",
                true);
    }

    public ClothesLoadedResponse loadAllClothes(long plantId) {
        List<Locker> lockers = lockerService.getAllBy(plantId);
        return loadClothes(lockers, plantId);
    }

    public ClothesLoadedResponse loadClothesFromLockersRange(int from, int to, long plantId) {
        List<Locker> lockers = lockerService.getLockers(from, to, plantId);
        return loadClothes(lockers, plantId);
    }

    private ClothesLoadedResponse loadClothes(List<Locker> lockers, long plantId) {
        Plant plant = plantService.getById(plantId);
        User user = userService.getDefaultUser();
        SimpleEmployee lastEmployee = new SimpleEmployee("", "");
        List<SimpleEmployee> skippedEmployees = new LinkedList<>();
        List<SimpleEmployee> employeesWithoutClothes = new LinkedList<>();
        Set<SimpleEmployee> doubledBoxes = new HashSet<>();
        int lockerNumber;
        int loadedEmployees = 0;
        scrapingService.connectToBoxesView(plant);
        for(Locker l : lockers) {
            lockerNumber = l.getLockerNumber();
            scrapingService.goToLocker(lockerNumber);
            List<Element> boxesRows = scrapingService.getBoxesRows();
            for(Element row : boxesRows) {
                SimpleEmployee simpleEmployee =
                        scrapingService.getSimpleEmployee(row);
                try {
                    Employee employee = employeeService.getBy(
                            simpleEmployee, plant);
                    if(employee.getClothes().size() > 0) continue;
                    scrapingService.loadBox(row);
                    List<Cloth> clothes = scrapingService.getClothes(employee);
                    clothService.loadClothes(clothes, employee, user);
                    loadedEmployees++;
                    lastEmployee = simpleEmployee;
                } catch (SkippedEmployeeException e) {
                    skippedEmployees.add(e.getEmployee());
                } catch (DoubledBoxException e) {
                    doubledBoxes.add(e.getEmployee());
                } catch (EmptyElementException e) {
                    employeesWithoutClothes.add(simpleEmployee);
                    continue;
                }
            }
        }
        return new ClothesLoadedResponse(
                loadedEmployees,
                lastEmployee,
                employeesWithoutClothes,
                doubledBoxes,
                skippedEmployees);
    }

    private int createEmployeesBoxesAndLockers(
            List<Element> boxesRows,
            Plant plant,
            Department department,
            Location location,
            long clientId) {
        Element row;
        int lockerNumber;
        int capacity;
        List<Locker> lockers = new LinkedList<>();
        List<Box> boxes = new LinkedList<>();
        Employee employee;
        int previousLockerNumber = scrapingService.getFirstLockerNumber(boxesRows);
        for(int i = 0; i < boxesRows.size(); i++) {
            row = boxesRows.get(i);
            lockerNumber = scrapingService.getLockerNumber(row);
            if ((previousLockerNumber != lockerNumber) ||
                    (i == boxesRows.size() - 1)) {
                if (i == boxesRows.size() - 1) {
                    SimpleEmployee simpleEmployee =
                            scrapingService.getSimpleEmployee(row);
                    employee = employeeService.createEmployee(simpleEmployee, clientId);
                    boxes.add(scrapingService.createBox(row, employee));
                }
                capacity = scrapingService.getLockerCapacity(boxes);
                boxes = boxService.createMissingEmptyBoxes(boxes, capacity);
                lockers.add(lockerService.create(
                        previousLockerNumber,
                        boxes,
                        capacity,
                        plant,
                        department,
                        location));
                boxes.clear();
                SimpleEmployee simpleEmployee =
                        scrapingService.getSimpleEmployee(row);
                employee = employeeService.createEmployee(simpleEmployee, clientId);
                boxes.add(scrapingService.createBox(row, employee));
                previousLockerNumber = lockerNumber;
            } else {
                SimpleEmployee simpleEmployee =
                        scrapingService.getSimpleEmployee(row);
                employee = employeeService.createEmployee(simpleEmployee, clientId);
                boxes.add(scrapingService.createBox(row, employee));
            }
        }
        return lockers.size();
    }


    public void test() {
        long plantId = 1l;
        List<Locker> allBy = lockerService.getAllBy(plantId);
        System.out.println("");
    }
}
