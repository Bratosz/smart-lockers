package pl.bratosz.smartlockers.service;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.DoubledBoxException;
import pl.bratosz.smartlockers.exception.EmptyElementException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.scraping.Scrapper;

import java.util.*;
import java.util.stream.Collectors;

import static pl.bratosz.smartlockers.scraping.TableSelector.TABLE_OF_BOXES;
import static pl.bratosz.smartlockers.scraping.TableSelector.TABLE_OF_EMPLOYEE_CLOTHES;

@Service
public class ScrapingService {

    private BoxService boxService;
    private ClothService clothService;
    private UserService userService;
    private Scrapper scrapper;
    private User user;


    public ScrapingService(BoxService boxService,
                           ClothService clothService,
                           UserService userService,
                           Scrapper scrapper
    ) {
        this.boxService = boxService;
        this.clothService = clothService;
        this.userService = userService;
        this.scrapper = scrapper;
        this.user = userService.getDefaultUser();
    }


    private void loadUser(long userId) {
        User user = userService.getUserById(userId);
        this.user = user;
    }

    public void loadBox(Element row) {
        scrapper.clickViewButton(row);
    }

    public List<Cloth> getClothes(
            Employee employee) throws EmptyElementException {
        Client client = employee
                .getBox().getLocker().getPlant().getClient();
        Elements clothesTable = scrapper
                .selectAsSecondaryTable(TABLE_OF_EMPLOYEE_CLOTHES);
        return scrapper.getClothes(clothesTable, client);
    }

    /**
     * Check status of box by number in klsonline24.pl
     * service
     *
     * @return 1 if employee exist,
     * 0 if box is empty,
     * -1 if there is another
     * employee at that box
     */
    public int checkBoxStatusBy(Employee employee) {
        Box box = employee.getBox();
        Elements boxTable;
        try {
            boxTable = goToBoxTableBody(box);
        } catch (EmptyElementException e) {
            return 0;
        }
        String lastName = scrapper.getLastName(boxTable);
        String firstName = scrapper.getFirstName(boxTable);
        if (employee.getLastName().equals(lastName)
                && employee.getFirstName().equals(firstName)) {
            return 1;
        } else {
            return -1;
        }
    }

    public SimpleEmployee getEmployee(Box box) throws DoubledBoxException {
        Elements boxTable = null;
        try {
            boxTable = goToBoxTableBody(box);
        } catch (EmptyElementException e) {
            return new SimpleEmployee("", "");
        }
        return createSimpleEmployee(boxTable);
    }

    private SimpleEmployee createSimpleEmployee(Elements boxTableBody) throws DoubledBoxException {
        if (boxTableBody.select("tr").size() > 1) {
            throw new DoubledBoxException();
        } else {
            return getSimpleEmployee(boxTableBody
                    .select("tr")
                    .stream()
                    .findFirst()
                    .get());
        }
    }

    private Elements goToBoxTableBody(Box box) throws EmptyElementException {
        Plant plant = box.getLocker().getPlant();
        scrapper.createConnection(plant);
        scrapper.goToBoxesView();
        scrapper.find(box);
        Elements boxTableBody = scrapper.selectAsMainTable(TABLE_OF_BOXES);
        if (boxTableBody.size() > 0) {
            return boxTableBody;
        } else {
            throw new EmptyElementException("Element must not be empty");
        }
    }

    private boolean boxIsDuplicated(int boxNumber, List<Integer> duplicates) {
        if (duplicates.contains(boxNumber)) {
            return true;
        } else {
            return false;
        }
    }

//    public Box loadEmployee(long boxId) {
//        Box box = boxService.getBoxById(boxId);
//        Plant plant = box.getLocker().getPlant();
//        Client client = plant.getClient();
//
//        scrapper.createConnection(plant);
//        scrapper.goToBoxesView();
//        scrapper.find(box);
//        scrapper.clickViewButton();
//        Elements clothesTable = scrapper.selectAsMainTable(TABLE_OF_EMPLOYEE_CLOTHES);
//        List<Cloth> clothes = scrapper.getClothes(clothesTable, client);
//        User user = userService.getDefaultUser();
//
//        String firstName = scrapper.getEmployeeFirstName();
//        String lastName = scrapper.getEmployeeLastName();
//        String departmentName = scrapper.getDepartmentName();
//        employeeService.createEmployee(
//                clothes, departmentName, box, firstName, lastName);
//        return boxService.getBoxById(box.getId());
//    }

//    public StandardResponse updateClothes(Plant plant) {
//
//        scrapper.createConnection(plant);
//        scrapper.goToClothesView();
//        scrapper.selectAsMainTable(TableSelector.ALL_CLOTHES_TABLE);
//        Elements clothesRows = scrapper.getMainTableBody();
//        updateClothes(clothesRows, plant);
//
//        return new StandardResponse("cośtam", true);
//    }

    public void connectToBoxesView(Plant plant) {
        scrapper.createConnection(plant);
        scrapper.goToBoxesView();
    }

    public void goToLocker(int lockerNumber) {
        scrapper.findLocker(lockerNumber);
    }

    public List<Element> getBoxesRows() {
        return scrapper.selectAsMainTable(TABLE_OF_BOXES);
    }

    public List<Element> getSortedBoxesRows() {
        scrapper.selectAsMainTable(TABLE_OF_BOXES);
        return sortBoxesRowsByLockerNumber(
                scrapper.getMainTableBody());
    }

//    private List<SimpleEmployee> updateClothes(Elements clothesRows, Plant plant) {
//        Client client = plant.getClient();
//        List<Cloth> updatedClothes = new LinkedList<>();
//        List<SimpleEmployee> skippedEmployees = new LinkedList<>();
//        Set<SimpleEmployee> doubledBoxes = new HashSet<>();
//        SimpleEmployee actualEmployee;
//        SimpleEmployee previousEmployee = getSimpleEmployee(clothesRows.first());
//        for (Element row : clothesRows) {
//            actualEmployee = getSimpleEmployee(row);
//            if (!(previousEmployee.equals(actualEmployee))
//                    || (row.equals(clothesRows.last()))) {
//                if (row.equals(clothesRows.last())) {
//                    updatedClothes.add(loadCloth(row, client));
//                }
//                try {
//                    Employee employee = employeeService.getBy(previousEmployee, plant);
//                    clothService.updateClothes(updatedClothes, employee, user);
//                } catch (SkippedEmployeeException e) {
//                    skippedEmployees.add(e.getEmployee());
//                } catch (DoubledBoxException e) {
//                    doubledBoxes.add(e.getEmployee());
//                } finally {
//                    updatedClothes.clear();
//                }
//            } else {
//                updatedClothes.add(loadCloth(row, client));
//            }
//        }
//        return skippedEmployees;
//    }

    private Cloth loadCloth(Element row, Client client) {
        return scrapper.getCloth(row, client);
    }

    public SimpleEmployee getSimpleEmployee(Element row) {
        String firstName = scrapper.getFirstName(row);
        String lastName = scrapper.getLastName(row);
        String comment = scrapper.getComment(row);
        String departmentAlias = getDepartmentAlias(row);
        int lockerNumber = scrapper.getLockerNumber(row);
        int boxNumber = scrapper.getBoxNumber(row);
        return new SimpleEmployee(
                firstName,
                lastName,
                comment,
                departmentAlias,
                lockerNumber,
                boxNumber);
    }

    public int getLockerNumber(Element row) {
        return scrapper.getLockerNumber(row);
    }

    public int getFirstLockerNumber(List<Element> boxesRows) {
        return scrapper.getLockerNumber(boxesRows.get(0));
    }

    public Box createBox(Element row, Employee employee) {
        int boxNumber = scrapper.getBoxNumber(row);
        return boxService.create(
                boxNumber, employee);
    }

    public int getLockerCapacity(List<Box> boxes) {
        int highestBoxNumber = boxes
                .stream()
                .mapToInt(b -> b.getBoxNumber())
                .max().orElse(10);
        if (highestBoxNumber <= 10) {
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

    private String getDepartmentAlias(Element r) {
        return r.select("td").get(0).text();
    }


}
