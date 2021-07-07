package pl.bratosz.smartlockers.service;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.DoubledBoxException;
import pl.bratosz.smartlockers.exception.SkippedEmployeeException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.scraping.Scrapper;
import pl.bratosz.smartlockers.scraping.TableSelector;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static pl.bratosz.smartlockers.scraping.TableSelector.TABLE_OF_BOXES;
import static pl.bratosz.smartlockers.scraping.TableSelector.TABLE_OF_EMPLOYEE_CLOTHES;

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
        this.user = userService.getDefaultUser();
    }



    private void loadUser(long userId) {
        User user = userService.getUserById(userId);
        this.user = user;
    }

    public StandardResponse loadClothes(long plantId) {
        Plant plant = plantService.getById(plantId);
        Client client = plant.getClient();
        List<SimpleEmployee> skippedEmployees = new LinkedList<>();
        Set<SimpleEmployee> doubledBoxes = new HashSet<>();
        scrapper.createConnection(plant);
        scrapper.goToBoxesView();
        scrapper.selectAsMainTable(TABLE_OF_BOXES);
        scrapper.getMainTableBody()
                .stream()
                .forEach(boxRow -> {
                    try {
                        Employee employee = getEmployee(boxRow, plant);
                        scrapper.clickViewButton(boxRow);
                            Elements clothesTable = scrapper
                                    .selectAsSecondaryTable(TABLE_OF_EMPLOYEE_CLOTHES);
                            List<Cloth> clothes = scrapper.getClothes(clothesTable, client);
                            clothService.updateClothes(
                                    clothes,
                                    employee,
                                    user);
                    } catch (SkippedEmployeeException e) {
                        skippedEmployees.add(
                                e.getEmployee());
                    } catch (DoubledBoxException e) {
                        doubledBoxes.add(
                                e.getEmployee());
                    }
                });

        return new StandardResponse("" , true);
    }

    private Employee getEmployee(
            Element boxRow,
            Plant plant) throws SkippedEmployeeException, DoubledBoxException {
        SimpleEmployee employee = scrapper.getEmployee(boxRow);
        return employeeService.getBy(
                employee, plant);
    }

    public Box updateEmployeeClothes(
            long boxId,
            long userId) throws IOException {
        loadUser(userId);
        Box box = boxService.getBoxById(boxId);
        Plant plant = box.getLocker().getPlant();
        Client client = plant.getClient();
        Employee employee = (Employee) box.getEmployee();

        scrapper.createConnection(plant);
        scrapper.goToBoxesView();
        scrapper.find(box);
        scrapper.clickViewButton();
        Elements boxTable = scrapper.selectAsMainTable(TABLE_OF_BOXES);
        Elements clothesTable = scrapper
                .selectAsSecondaryTable(TABLE_OF_EMPLOYEE_CLOTHES);
        if (employee.getLastName().equals(scrapper.getLastName(boxTable))
        || employee.getFirstName().equals(scrapper.getLastName(boxTable))) {
            List<Cloth> actualClothes = scrapper.getClothes(clothesTable, client);
            clothService.updateClothes(actualClothes, employee, user);
            return box;
        } else {
            throw new IOException("Last names are different");
        }

    }

    private boolean boxIsDuplicated(int boxNumber, List<Integer> duplicates) {
        if (duplicates.contains(boxNumber)) {
            return true;
        } else {
            return false;
        }
    }

    public Box loadEmployee(long boxId) {
        Box box = boxService.getBoxById(boxId);
        Plant plant = box.getLocker().getPlant();
        Client client = plant.getClient();

        scrapper.createConnection(plant);
        scrapper.goToBoxesView();
        scrapper.find(box);
        scrapper.clickViewButton();
        Elements clothesTable = scrapper.selectAsMainTable(TABLE_OF_EMPLOYEE_CLOTHES);
        List<Cloth> clothes = scrapper.getClothes(clothesTable, client);
        User user = userService.getDefaultUser();

        String firstName = scrapper.getEmployeeFirstName();
        String lastName = scrapper.getEmployeeLastName();
        String departmentName = scrapper.getDepartmentName();
        employeeService.createEmployee(
                clothes, departmentName, box, firstName, lastName);
        return boxService.getBoxById(box.getId());
    }

    public StandardResponse updateClothes(long plantId) {
        Plant plant = plantService.getById(plantId);

        scrapper.createConnection(plant);
        scrapper.goToClothesView();
        scrapper.selectAsMainTable(TableSelector.ALL_CLOTHES_TABLE);
        Elements clothesRows = scrapper.getMainTableBody();
        updateClothes(clothesRows, plant);

        return new StandardResponse("cośtam", true);
    }


    public StandardResponse loadLockersWithBoxesAndEmployees(long plantId) {
        Plant plant = plantService.getById(plantId);
        scrapper.createConnection(plant);
        scrapper.goToBoxesView();
        scrapper.selectAsMainTable(TABLE_OF_BOXES);
        List<Element> boxesRows = sortBoxesRowsByLockerNumber(
                scrapper.getMainTableBody());
        createEmployeesBoxesAndLockers(boxesRows, plant);
        return new StandardResponse("Udało się", true);
    }

    private List<SimpleEmployee> updateClothes(Elements clothesRows, Plant plant) {
        Client client = plant.getClient();
        List<Cloth> updatedClothes = new LinkedList<>();
        List<SimpleEmployee> skippedEmployees = new LinkedList<>();
        Set<SimpleEmployee> doubledBoxes = new HashSet<>();
        SimpleEmployee actualEmployee;
        SimpleEmployee previousEmployee = createSimpleEmployee(clothesRows.first());
        for(Element row : clothesRows) {
            actualEmployee = createSimpleEmployee(row);
            if(!(previousEmployee.equals(actualEmployee))
                    || (row.equals(clothesRows.last()))) {
                if(row.equals(clothesRows.last())){
                    updatedClothes.add(loadCloth(row, client));
                }
                try {
                    Employee employee = employeeService.getBy(previousEmployee, plant);
                    clothService.updateClothes(updatedClothes, employee, user);
                } catch (SkippedEmployeeException e) {
                    skippedEmployees.add(e.getEmployee());
                } catch (DoubledBoxException e) {
                    doubledBoxes.add(e.getEmployee());
                } finally {
                    updatedClothes.clear();
                }
            } else {
                updatedClothes.add(loadCloth(row, client));
            }
        }
        return skippedEmployees;
    }

    private Cloth loadCloth(Element row, Client client) {
        return scrapper.getCloth(row, client);
    }

    private SimpleEmployee createSimpleEmployee(Element row) {
        String firstName = scrapper.getFirstName(row);
        String lastName = scrapper.getLastName(row);
        int lockerNumber = scrapper.getLockerNumber(row);
        int boxNumber = scrapper.getBoxNumber(row);
        return new SimpleEmployee(
                firstName,
                lastName,
                lockerNumber,
                boxNumber);
    }

    private List<Locker> createEmployeesBoxesAndLockers (
            List<Element> boxesRows,
            Plant plant) {
        Client client = plant.getClient();
        long clientId = client.getId();
        Location location = locationService.getSurrogateBy(client);
        Department department = departmentService.getSurrogateBy(client);
        Element row;
        int lockerNumber;
        int capacity;
        List<Locker> lockers = new LinkedList<>();
        List<Box> boxes = new LinkedList<>();
        Employee employee;
        int previousLockerNumber = scrapper.getLockerNumber(boxesRows.get(0));
        for(int i = 0; i < boxesRows.size(); i++) {
            row = boxesRows.get(i);
            lockerNumber = scrapper.getLockerNumber(row);
            if((previousLockerNumber != lockerNumber) ||
                    (i == boxesRows.size() - 1)) {
                if(i == boxesRows.size() -1) {
                    employee = createEmployee(row, clientId);
                    boxes.add(createBox(row, employee));
                }
                    capacity = getLockerCapacity(boxes);
                    boxes = boxService.createMissingEmptyBoxes(boxes, capacity);
                    lockers.add(lockerService.create(
                            previousLockerNumber,
                            boxes,
                            capacity,
                            plant,
                            department,
                            location));
                    boxes.clear();
                    employee = createEmployee(row, clientId);
                    boxes.add(createBox(row, employee));
                previousLockerNumber = lockerNumber;
            } else {
                employee = createEmployee(row, clientId);
                boxes.add(createBox(row, employee));
            }

        }
        return lockers;
    }

    private Box createBox(Element row, Employee employee) {
        int boxNumber = scrapper.getBoxNumber(row);
        return boxService.create(
                boxNumber, employee);
    }

    private Employee createEmployee(Element row, long clientId) {
        String firstName = scrapper.getFirstName(row);
        String lastName = scrapper.getLastName(row);
        String comment = scrapper.getComment(row);
        Department department = getDepartment(row, clientId);
        return employeeService.create(
                firstName, lastName, comment, department);
    }

    private int getLockerCapacity(List<Box> boxes) {
        int highestBoxNumber = boxes
                .stream()
                .mapToInt(b -> b.getBoxNumber())
                .max().orElse(10);
        if(highestBoxNumber <= 10) {
            return 10;
        } else if (highestBoxNumber <= 20) {
            return 20;
        } else if (highestBoxNumber <= 30) {
            return 30;
        } else if (highestBoxNumber <= 40) {
            return 40;
        } else {
            return 1000;
        }
    }

    private List<Element> sortBoxesRowsByLockerNumber(Elements boxesRows) {
        return boxesRows
                .stream()
                .sorted(Comparator.comparing(
                        r -> Integer.parseInt(
                                r.select("td").get(4).text())))
                .collect(Collectors.toList());
    }

    private Department getDepartment(Element r, long clientId) {
        String departmentName = r.select("td").get(0).text();
        return departmentService.getByAliasAndClientId(
                departmentName,
                clientId);
    }



    public void test(long plantId) {
        Plant byId = plantService.getById(plantId);
        scrapper.createConnection(byId);
        scrapper.goToBoxesView();
        scrapper.clickViewButton(3);
    }



}
