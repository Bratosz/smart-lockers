package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.comparators.BoxNumberSorter;
import pl.bratosz.smartlockers.comparators.PlantNumberSorter;
import pl.bratosz.smartlockers.comparators.LockerNumberSorter;
import pl.bratosz.smartlockers.exception.BoxNotAvailableException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.EmployeeGeneralRepository;
import pl.bratosz.smartlockers.repository.EmployeesRepository;

import java.util.*;

@Service
public class EmployeeService {
    private EmployeesRepository employeesRepository;
    private EmployeeGeneralRepository employeeGeneralRepository;
    private BoxService boxService;
    private PlantService plantService;
    private DepartmentService departmentService;

    public EmployeeService(EmployeesRepository employeesRepository, EmployeeGeneralRepository employeeGeneralRepository, BoxService boxesService,
                           PlantService plantService, DepartmentService departmentService) {
        this.employeesRepository = employeesRepository;
        this.employeeGeneralRepository = employeeGeneralRepository;
        this.boxService = boxesService;
        this.plantService = plantService;
        this.departmentService = departmentService;
    }

    public List<Employee> getAllEmployees() {
        return employeesRepository.findAll();
    }

    public List<Employee> getEmployeesByLastName(String lastName) {
        return employeesRepository.getEmployeesByLastName(lastName);
    }

    public Employee createEmployee(long plantId, long departmentId, int lockerNumber,
                                   int boxNumber, String firstName, String lastName) throws BoxNotAvailableException {
        Department department = departmentService.getById(departmentId);
        Box box = boxService.getBox(plantId, lockerNumber, boxNumber);
        if (box.getBoxStatus().equals(Box.BoxStatus.OCCUPY)) {
            throw new BoxNotAvailableException("Nie udało się dodać pracownika " +
                    " bo szafka o numerze " + lockerNumber + "/" + boxNumber + " jest zajęta");
        }
        Employee employee = new Employee(firstName, lastName, department, true);
        employee.setBox(box);
        return employeesRepository.save(employee);

    }

    public Employee createEmployee(int plantNumber, String departmentName, int lockerNumber,
                                   int boxNumber, String firstName, String lastName) {
        Plant plant = plantService.getByNumber(plantNumber);
        Department department = departmentService.getByNameAndPlantNumber(departmentName, plantNumber);
        Box box = boxService.getBox(plant.getId(), lockerNumber, boxNumber);
        box.setBoxStatus(Box.BoxStatus.OCCUPY);
        Employee employee = new Employee();
        employee.setDepartment(department);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        return employeesRepository.save(employee);
    }

    public Employee createEmployee(Employee employee) {
        Box box = employee.getBox();
        box.setBoxStatus(Box.BoxStatus.OCCUPY);
        return employeesRepository.save(employee);
    }

    public Employee createEmployeeAndAssignToBox(
            int plantNumber, Department department, Location location, Employee employee) {
        Box emptyBox;
        //change location if there is no free boxes
        try {
            emptyBox = boxService.findNextFreeBox(department, plantNumber, location);
        } catch (BoxNotAvailableException e) {
            e.getMessage();
            return null;
        }
        emptyBox.setBoxStatus(Box.BoxStatus.OCCUPY);
        emptyBox.setEmployee(employee);
        employee.setBox(emptyBox);

        return employeesRepository.save(employee);
    }

    public Employee getEmployeeById(Long id) {
        return employeesRepository.getEmployeeById(id);
    }

    public EmployeeGeneral getGeneralEmployeeById(long id) {
        return employeeGeneralRepository.getById(id);
    }

    public List<Employee> getByFirstNameAndLastName(String firstName, String lastName) {
        List<Employee> filteredEmployees = new LinkedList<>();
        List<Employee> employees = employeesRepository.getByFirstNameAndLastName(firstName, lastName);
        for (Employee e : employees) {
            if(e.isActive()) {
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

    public Box changeEmployeeBoxOnNextFree(int lockerNumber, int boxNumber,
                                           long plantId, Department targetDep,
                                           Location targetLocation, int targetPlantNumber) {
        Box oldBox = boxService.getBox(plantId, lockerNumber, boxNumber);
        Box freeBox = boxService.findNextFreeBox(targetDep, targetPlantNumber, targetLocation);
        Employee employee = boxService.getEmployeeToChangeBox(oldBox);
        return boxService.setEmployee(employee, freeBox);
    }

    public Box changeEmployeeBox(int lockerNumber, int boxNumber, long plantId,
                                 int targetLockerNumber, int targetBoxNumber,
                                 long targetPlantId) throws BoxNotAvailableException {
        Box newBox = boxService.getBox(targetPlantId, targetLockerNumber, targetBoxNumber);

        if (newBox.getBoxStatus().equals(Box.BoxStatus.OCCUPY)) {
            throw new BoxNotAvailableException("Szafka o numerze: " + targetLockerNumber + "/" + targetBoxNumber
                    + "jest niedostępna");
        } else {
            Box box = boxService.getBox(plantId, lockerNumber, boxNumber);
            Employee employee = boxService.getEmployeeToChangeBox(box);
            return boxService.setEmployee(employee, newBox);
        }
    }

    public Box dismissById(long id) {
        EmployeeGeneral empToDismiss = getGeneralEmployeeById(id);
        Box box = empToDismiss.getBox();
        Box releasedBox = boxService.dismissEmployee(box, empToDismiss);
        return releasedBox;
    }

    public Employee save(Employee employee) {
        return employeesRepository.save(employee);
    }


    public Employee changeEmployeeLastName(String lastName, Long id) {
        Employee employee = getEmployeeById(id);
        employee.setLastName(lastName);
        return employeesRepository.save(employee);
    }

    public Employee changeEmployeeFirstNameById(String firstName, Long id) {
        Employee employee = getEmployeeById(id);
        employee.setFirstName(firstName);
        return employeesRepository.save(employee);
    }

    public Integer deleteEmployeeById(Long id) {
        return employeesRepository.deleteEmployeeById(id);
    }


    public Employee changeEmployeeFirstNameAndLastNameById(Employee updatedEmployee, Long id) {
        Employee employee = getEmployeeById(id);
        employee.setFirstName(updatedEmployee.getFirstName());
        employee.setLastName(updatedEmployee.getLastName());
        return employeesRepository.save(employee);
    }

    public Employee changeDepartment(Department department, Long id) {
        Employee employee = getEmployeeById(id);
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
        if(employees.size() == 1) {
            return employees.get(0);
        } else if (employees.size() > 1) {
            for(Employee e : employees) {
                if(e.getBox().getLocker().getLockerNumber() == lockerNo && e.getBox().getBoxNumber() == boxNo){
                    return e;
                }
            }
        }
        return null;
    }

    public Employee getOneEmployee(String firstName, String lastName) {
        List<Employee> employees = getByFirstNameAndLastName(firstName, lastName);
        if(employees.size() == 1) {
            return employees.get(0);
        } else {
            return employeesRepository.getEmployeeById((long)1);
        }
    }

    public List<Employee> getEmployeesByFirstName(String firstName) {
        return employeesRepository.getEmployeesByFirstName(firstName);
    }
}
