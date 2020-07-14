package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.comparators.BoxNumberSorter;
import pl.bratosz.smartlockers.comparators.DepartmentNumberSorter;
import pl.bratosz.smartlockers.comparators.LockerNumberSorter;
import pl.bratosz.smartlockers.exception.BoxNotAvailableException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.EmployeesRepository;
import pl.bratosz.smartlockers.repository.LockersRepository;

import java.util.*;

@Service
public class EmployeeService {
    private EmployeesRepository employeesRepository;
    private LockersRepository lockersRepository;
    private BoxesService boxesService;

    public EmployeeService(EmployeesRepository employeesRepository, LockersRepository lockersRepository, BoxesService boxesService) {
        this.employeesRepository = employeesRepository;
        this.lockersRepository = lockersRepository;
        this.boxesService = boxesService;
    }

    public List<Employee> getAllEmployees() {
        return employeesRepository.findAll();
    }

    public List<Employee> getEmployeesByLastName(String lastName) {
        return employeesRepository.getEmployeesByLastName(lastName);
    }

    public Employee createEmployee(int plantNumber,
                                   int lockerNumber, int boxNumber, Employee employee) throws BoxNotAvailableException {
        Box box = lockersRepository.getBox(plantNumber, lockerNumber, boxNumber);
        if (box.getBoxStatus().equals(Box.BoxStatus.OCCUPY)) {
            throw new BoxNotAvailableException("Nie udało się dodać pracownika " +
                    employee.getFirstName() + " " + employee.getLastName()
                    + " bo szafka o numerze " + lockerNumber + "/" + boxNumber + " jest zajęta");
        }
        box.setBoxStatus(Box.BoxStatus.OCCUPY);

        String lastName = employee.getLastName().toUpperCase();
        String firstName = employee.getFirstName().toUpperCase();
        Department department = employee.getDepartment();

        Employee correctedEmployee = new Employee(firstName, lastName, department);

        box.setEmployee(correctedEmployee);
        return employeesRepository.save(correctedEmployee);

    }

    public Employee createEmployee(Employee employee) {
        Set<Box> boxes = employee.getBoxes();
        boxes.forEach(b -> b.setBoxStatus(Box.BoxStatus.OCCUPY));
        return employeesRepository.save(employee);
    }

    public Employee createEmployeeAndAssignToBox(Department department, Location location, Employee employee) {
        int plantNumber;
        //chosing department number by location and department
        if (department.equals(Department.METAL) || (department.equals(Department.JIT) && location.equals(Locker.Location.OLDSIDE))) {
            plantNumber = Locker.DepartmentNumber.DEP_384;
        } else {
            plantNumber = Locker.DepartmentNumber.DEP_385;
        }

        //change location if there is no free boxes
        try {
            boxesService.findNextFreeBox(department, plantNumber, location);
        } catch (BoxNotAvailableException e) {
            if (location.equals(Locker.Location.OLDSIDE)) {
                location = Locker.Location.NEWSIDE;
            } else if (location.equals(Locker.Location.NEWSIDE)) {
                location = Locker.Location.OLDSIDE;
            } else if (department.equals(Department.JIT) && location.equals(Locker.Location.OLDSIDE)) {
                plantNumber = Locker.DepartmentNumber.DEP_385;
                location = Locker.Location.NEWSIDEUPSTAIRS;

                if (boxesService.findNextFreeBox(department, plantNumber, location).equals(null)) {
                    plantNumber = Locker.DepartmentNumber.DEP_384;
                    location = Locker.Location.NEWSIDE;
                }
            }
        }

        Box freeBox = boxesService.findNextFreeBox(department, plantNumber, location);
        freeBox.setBoxStatus(Box.BoxStatus.OCCUPY);
        freeBox.setEmployee(employee);

        Set<Box> boxes = new HashSet<>();
        boxes.add(freeBox);
        employee.setBoxes(boxes);

        return employeesRepository.save(employee);
    }

    public void deleteFromBoxEmployeeById(Long id) {
        Employee employeeById = employeesRepository.getEmployeeById(id);
        employeeById.getBoxes().stream().forEach(b -> boxesService.releaseEmployeeFromBox(b));
        employeesRepository.deleteEmployeeById(id);
    }

    public Employee getEmployeeById(Long id) {
        return employeesRepository.getEmployeeById(id);
    }

    public List<Employee> getByFirstNameAndLastName(String firstName, String lastName) {
        List<Employee> filteredEmployees = new LinkedList<>();
        List<Employee> employees = employeesRepository.getEmployeesByFirstNameAndLastName(firstName, lastName);
        for (Employee employee : employees) {
            if (employee.getBoxes().size() >= 1) {
                filteredEmployees.add(employee);
            }
        }
        return filteredEmployees;
    }

    public List<Employee> sortEmployeesByDepartmentBoxAndLocker(List<Employee> employeesToSort) {
        Collections.sort(employeesToSort, new DepartmentNumberSorter()
                .thenComparing(new LockerNumberSorter())
                .thenComparing(new BoxNumberSorter()));

        return employeesToSort;
    }

    public Box changeEmployeeBoxOnNextFree(int lockerNumber, int boxNumber,
                                           Locker.DepartmentNumber depNumber, Department targetDep,
                                           Locker.Location targetLocation, Locker.DepartmentNumber targetDepNumber) {

        Box freeBox = boxesService.findNextFreeBox(targetDep, targetDepNumber, targetLocation);
        Employee employee = removeEmployeeFromBox(lockerNumber, boxNumber, depNumber);

        return boxesService.setEmployee(employee, freeBox);
    }

    public Box changeEmployeeBox(int lockerNumber, int boxNumber, Locker.DepartmentNumber departmentNumber,
                                 Integer targetLockerNumber, Integer targetBoxNumber,
                                 Locker.DepartmentNumber targetDepartmentNumber) throws BoxNotAvailableException {
        Box newBox = boxesService.getBox(targetLockerNumber, targetBoxNumber, targetDepartmentNumber);

        if (newBox.getBoxStatus().equals(Box.BoxStatus.OCCUPY)) {
            throw new BoxNotAvailableException("Szafka o numerze: " + targetLockerNumber + "/" + targetBoxNumber
                    + "jest niedostępna");
        } else {
            Employee employee = removeEmployeeFromBox(lockerNumber, boxNumber, departmentNumber);
            return boxesService.setEmployee(employee, newBox);
        }
    }

    private Employee removeEmployeeFromBox(int lockerNumber, int boxNumber,
                                           Locker.DepartmentNumber departmentNumber) {
        Box box = boxesService.getBox(lockerNumber, boxNumber, departmentNumber);
        return boxesService.releaseEmployeeFromBox(box);
    }

    public Set<Box> dismissById(Long id) {
        Employee employee = getEmployeeById(id);
        Set<Box> releasedBoxes = new HashSet<>();
        for (Box box : employee.getBoxes()) {
            Box releasedBox = boxesService.dismissEmployee(box);
            releasedBoxes.add(releasedBox);
        }
        return releasedBoxes;
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
                employeesRepository.getEmployeesByFirstNameAndLastName(firstName, lastName);
        return filterEmployeesByFullBoxNumber(lockerNo, boxNo, employees);

    }

    private Employee filterEmployeesByFullBoxNumber(int lockerNo, int boxNo, List<Employee> employees) {
        if(employees.size() == 1) {
            return employees.get(0);
        } else if (employees.size() > 1) {
            for(Employee e : employees) {
                if(e.isEmployeeHaveThisBox(lockerNo, boxNo)){
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
}
