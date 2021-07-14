package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.comparators.BoxNumberSorter;
import pl.bratosz.smartlockers.comparators.PlantNumberSorter;
import pl.bratosz.smartlockers.comparators.LockerNumberSorter;
import pl.bratosz.smartlockers.exception.BoxNotAvailableException;
import pl.bratosz.smartlockers.exception.EmptyElementException;
import pl.bratosz.smartlockers.exception.SkippedEmployeeException;
import pl.bratosz.smartlockers.exception.DoubledBoxException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.EmployeeGeneralRepository;
import pl.bratosz.smartlockers.repository.EmployeesRepository;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.managers.EmployeeManager;

import java.util.*;

import static pl.bratosz.smartlockers.model.Box.BoxStatus.OCCUPY;

@Service
public class EmployeeService {
    private EmployeesRepository employeesRepository;
    private EmployeeGeneralRepository employeeGeneralRepository;
    private BoxService boxService;
    private PlantService plantService;
    private DepartmentService departmentService;
    private SimpleBoxService simpleBoxService;
    private UserService userService;
    private ClothService clothService;
    private User user;
    private EmployeeManager employeeManager;
    private ScrapingService scrapingService;

    public EmployeeService(EmployeesRepository employeesRepository,
                           EmployeeGeneralRepository employeeGeneralRepository,
                           BoxService boxesService,
                           PlantService plantService,
                           DepartmentService departmentService,
                           UserService userService,
                           ClothService clothService,
                           EmployeeManager employeeManager, ScrapingService scrapingService) {
        this.employeesRepository = employeesRepository;
        this.employeeGeneralRepository = employeeGeneralRepository;
        this.boxService = boxesService;
        this.plantService = plantService;
        this.departmentService = departmentService;
        this.userService = userService;
        this.clothService = clothService;
        this.employeeManager = employeeManager;
        this.scrapingService = scrapingService;
    }

    private void loadUser(User user) {
        this.user = user;
    }

    private void loadUser(long userId) {
        User user = userService.getUserById(userId);
        this.user = user;
    }

    public List<Employee> getAllEmployees() {
        return employeesRepository.getAll();
    }

    public List<Employee> getEmployeesByLastName(String lastName, long clientId) {
        return employeesRepository.getEmployeesByLastName(lastName, clientId);
    }

    public Employee createEmployee(long boxId,
                                   long departmentId,
                                   String firstName,
                                   String lastName) throws BoxNotAvailableException {
        Department department = departmentService.getById(departmentId);
        Box box = boxService.getBoxById(boxId);
        if (box.getBoxStatus().equals(OCCUPY)) {
            throw new BoxNotAvailableException("Nie udało się dodać pracownika " +
                    " bo szafka o numerze "
                    + box.getLocker().getLockerNumber()
                    + "/" + box.getBoxNumber() + " jest zajęta");
        }
        Employee employee = new Employee(firstName, lastName, department, true);
        employee.addToBox(box);
        return employeesRepository.save(employee);
    }

    public Employee createEmployee(List<Cloth> clothes,
                                   String departmentName,
                                   Box box,
                                   String firstName,
                                   String lastName) {
        if (box.getBoxStatus().equals(OCCUPY)) {
            throw new BoxNotAvailableException("Box is occupy by"
                    + box.getEmployee().getLastName() + " " + box.getEmployee().getFirstName());
        }
        Department department = departmentService.getBy(departmentName, box);
        Employee employee = new Employee(firstName, lastName, department, true);
        employee.addToBox(box);
        employee.addClothes(clothes);
        return employeesRepository.save(employee);
    }

    public Employee create(
            String firstName,
            String lastName,
            Box box,
            String departmentName) {
        if (box.getBoxStatus().equals(OCCUPY)) {
            throw new BoxNotAvailableException("Szafka jest zajęta");
        }
        Department department = departmentService.getBy(departmentName, box);
        Employee employee = new Employee(firstName, lastName, department, true);
        employee.addToBox(box);
        return employeesRepository.save(employee);
    }

    public Employee createEmployee(int plantNumber,
                                   String departmentName,
                                   int lockerNumber,
                                   int boxNumber,
                                   String firstName,
                                   String lastName) {
        Plant plant = plantService.getByNumber(plantNumber);
        Department department = departmentService.getByNameAndPlantNumber(departmentName, plantNumber);
        Box box = boxService.getBox(plant.getId(), lockerNumber, boxNumber);
        box.setBoxStatus(OCCUPY);
        Employee employee = new Employee();
        employee.setDepartment(department);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        return employeesRepository.save(employee);
    }

    public Employee createEmployee(SimpleEmployee e,
                                   long clientId) {
        Department department = departmentService.getByAliasAndClientId(
                e.getDepartmentAlias(),
                clientId);
        return create(
                e.getFirstName(),
                e.getLastName(),
                e.getComment(),
                department);
    }


    public Employee createEmployeeAndAssignToBox(int plantNumber,
                                                 Department department,
                                                 Location location,
                                                 Employee employee) {
        Box emptyBox;
        //change location if there is no free boxes
        try {
            emptyBox = boxService.findNextFreeBox(department, plantNumber, location);
        } catch (BoxNotAvailableException e) {
            e.getMessage();
            return null;
        }
        emptyBox.setBoxStatus(OCCUPY);
        employee.addToBox(emptyBox);

        return employeesRepository.save(employee);
    }

    public Employee getById(Long id) {
        return employeesRepository.getEmployeeById(id);
    }

    public EmployeeGeneral getGeneralEmployeeById(long id) {
        return employeeGeneralRepository.getById(id);
    }

    public List<Employee> getByFirstNameAndLastName(String firstName,
                                                    String lastName) {
        List<Employee> filteredEmployees = new LinkedList<>();
        List<Employee> employees = employeesRepository.getByFirstNameAndLastName(firstName, lastName);
        for (Employee e : employees) {
            if (e.isActive()) {
                filteredEmployees.add(e);
            }
        }
        return filteredEmployees;
    }

    public List<Employee> sortByPlantBoxAndLocker(List<Employee> employeesToSort) {
        Collections.sort(employeesToSort, new PlantNumberSorter()
                .thenComparing(new LockerNumberSorter())
                .thenComparing(new BoxNumberSorter()));

        return employeesToSort;
    }


    public Employee dismissBy(long employeeId,
                              long userId) {
        loadUser(userId);
        Employee employee = getById(employeeId);
        Box box = employee.getBox();

        if (box.getBoxStatus().equals(OCCUPY)) {
            employee = employeeManager.dismiss(employee, user);
            return employeesRepository.save(employee);
        } else {
            return employee;
        }
    }

    public Employee save(Employee employee) {
        return employeesRepository.save(employee);
    }


    public Employee changeEmployeeLastName(String lastName, Long id) {
        Employee employee = getById(id);
        employee.setLastName(lastName);
        return employeesRepository.save(employee);
    }

    public Employee changeEmployeeFirstNameById(String firstName, Long id) {
        Employee employee = getById(id);
        employee.setFirstName(firstName);
        return employeesRepository.save(employee);
    }

    public Integer deleteEmployeeById(Long id) {
        return employeesRepository.deleteEmployeeById(id);
    }


    public Employee changeEmployeeFirstNameAndLastNameById(Employee updatedEmployee,
                                                           Long id) {
        Employee employee = getById(id);
        employee.setFirstName(updatedEmployee.getFirstName());
        employee.setLastName(updatedEmployee.getLastName());
        return employeesRepository.save(employee);
    }

    public Employee changeDepartment(Department department, Long id) {
        Employee employee = getById(id);
        employee.setDepartment(department);
        return employeesRepository.save(employee);
    }

    public Employee getEmployeeByFullNameAndFullBoxNumber(
            String firstName, String lastName, int lockerNo, int boxNo) {
        List<Employee> employees =
                employeesRepository.getByFirstNameAndLastName(firstName, lastName);
        return filterEmployeesByFullBoxNumber(lockerNo, boxNo, employees);

    }

    private Employee filterEmployeesByFullBoxNumber(int lockerNo, int boxNo, List<Employee> employees) {
        if (employees.size() == 1) {
            return employees.get(0);
        } else if (employees.size() > 1) {
            for (Employee e : employees) {
                if (e.getBox().getLocker().getLockerNumber() == lockerNo && e.getBox().getBoxNumber() == boxNo) {
                    return e;
                }
            }
        }
        return null;
    }

    public Employee getOneEmployee(String firstName, String lastName) {
        List<Employee> employees = getByFirstNameAndLastName(firstName, lastName);
        if (employees.size() == 1) {
            return employees.get(0);
        } else {
            return employeesRepository.getEmployeeById((long) 1);
        }
    }

    public List<Employee> getEmployeesByFirstName(String firstName) {
        return employeesRepository.getEmployeesByFirstName(firstName);
    }

    public Box changeEmployeeBox(long userId, int lockerNumber, int boxNumber, int plantNumber, int targetLockerNumber, int targetBoxNumber, int targetPlantNumber) {
        return new Box();
    }


    public Employee release(Employee employeeToRelease, Box box) {
        employeeToRelease.setAsPastBox(
                simpleBoxService.createSimpleBox(box, employeeToRelease));
        employeeToRelease.setBox(null);
        return employeesRepository.save(employeeToRelease);
    }

    public String relocate(
            long plantId,
            long departmentId,
            long locationId,
            long employeeId) {
        Box newBox = boxService.findNextFreeBox(plantId, departmentId, locationId);
        if (newBox.getLocker() == null) {
            return "Nie znaleziono wolnej szafki";
        }
        Employee employee = employeesRepository.getEmployeeById(employeeId);
        Box releasedBox = boxService.releaseBox(employee.getBox());
        SimpleBox simpleBox = new SimpleBox(releasedBox);
        employee.setAsPastBox(simpleBox);
        assignToBox(employee, newBox);
        return "Przypisano do szafki " + newBox.toString();
    }

    private Employee assignToBox(Employee employee, Box freeBox) {
        freeBox.setBoxStatus(OCCUPY);
        employee.addToBox(freeBox);
        return employeesRepository.save(employee);
    }

    public void create(
            String firstName,
            String lastName,
            String comment,
            Department department,
            Box box) {
        Employee employee = Employee.create(
                firstName,
                lastName,
                comment,
                department,
                box);
        employeesRepository.save(employee);
    }

    public Employee create(
            String firstName,
            String lastName,
            String comment,
            Department department) {
        Employee e = Employee.createWithoutBox(
                firstName,
                lastName,
                comment,
                department);
        return employeesRepository.save(e);
    }

    public Employee getBy(
            SimpleEmployee simpleEmployee,
            Plant plant) throws SkippedEmployeeException, DoubledBoxException {
        int lockerNumber = simpleEmployee.getLockerNumber();
        int boxNumber = simpleEmployee.getBoxNumber();
        Department department = departmentService.getByAliasAndClientId(
                simpleEmployee.getDepartmentAlias(), plant.getClient().getId());
        List<Employee> employees = employeesRepository.getBy(
                lockerNumber,
                boxNumber,
                plant,
                department);
        if (employees.size() > 1) {
            throw new DoubledBoxException(simpleEmployee);
        } else if(employees.size() == 0) {
            throw new SkippedEmployeeException(simpleEmployee);
        } else {
            Employee employee = employees.stream().findFirst().get();
            if (employee.getLastName().equals(simpleEmployee.getLastName())
                    && employee.getFirstName().equals(simpleEmployee.getFirstName())) {
                return employee;
            } else {
                throw new SkippedEmployeeException(simpleEmployee);
            }
        }
    }

    public StandardResponse update(long employeeId, long userId) {
        Employee employee = employeesRepository.getEmployeeById(employeeId);
        User user = userService.getUserById(userId);
        int status = scrapingService.checkBoxStatusBy(employee);
        if(status == 1) {
            try {
                List<Cloth> actualClothes = scrapingService.getClothes(employee);
                clothService.updateClothes(
                        actualClothes, employee, user);
                return new StandardResponse(
                        "Zaktualizowano ubrania", true);
            } catch (EmptyElementException e) {
               return new StandardResponse(
                       "Pracownik nie ma ubrań",
                       false);
            }
        } else if(status == 0) {
            return new StandardResponse(
                    "Szafka jest pusta", false);
        } else {
            try {
                SimpleEmployee simpleEmployee =
                        scrapingService.getEmployee(employee.getBox());
                return new StandardResponse(
                        "Inny pracownik w szafce: " +
                        simpleEmployee.getLastName() + " " +
                        simpleEmployee.getFirstName() + " " +
                        simpleEmployee.getDepartmentAlias(),
                        false);
            } catch (DoubledBoxException e) {
                return new StandardResponse(
                        "Znaleziono 2 szafki o tym numerze",
                        false);
            }
        }
    }

    public void save(List<Employee> employees) {
        employeesRepository.saveAll(employees);
    }
}
