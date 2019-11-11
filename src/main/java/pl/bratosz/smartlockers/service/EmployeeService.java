package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.comparators.BoxNumberSorter;
import pl.bratosz.smartlockers.comparators.DepartmentNumberSorter;
import pl.bratosz.smartlockers.comparators.LockerNumberSorter;
import pl.bratosz.smartlockers.exception.BoxNotAvailableException;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.Locker;
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

    public Employee createEmployee(Locker.DepartmentNumber departmentNumber,
                                   Integer lockerNumber, Integer boxNumber, Employee employee) throws BoxNotAvailableException{
        Box box = lockersRepository.getBox(departmentNumber, lockerNumber, boxNumber);
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

    public Employee createEmployeeAndAssignToBox(Department department, Locker.Location location, Employee employee) {
        Locker.DepartmentNumber departmentNumber;
        //chosing department number by location and department
        if (department.equals(Department.METAL) || (department.equals(Department.JIT) && location.equals(Locker.Location.OLDSIDE))) {
            departmentNumber = Locker.DepartmentNumber.DEP_384;
        } else {
            departmentNumber = Locker.DepartmentNumber.DEP_385;
        }

        //change location if there is no free boxes
        try{boxesService.findNextFreeBox(department, departmentNumber, location);}
        catch (BoxNotAvailableException e) {
            if (location.equals(Locker.Location.OLDSIDE)) {
                location = Locker.Location.NEWSIDE;
            } else if (location.equals(Locker.Location.NEWSIDE)) {
                location = Locker.Location.OLDSIDE;
            } else if (department.equals(Department.JIT) && location.equals(Locker.Location.OLDSIDE)) {
                departmentNumber = Locker.DepartmentNumber.DEP_385;
                location = Locker.Location.NEWSIDEUPSTAIRS;

                if (boxesService.findNextFreeBox(department, departmentNumber, location).equals(null)) {
                    departmentNumber = Locker.DepartmentNumber.DEP_384;
                    location = Locker.Location.NEWSIDE;
                }
            }
        }

        Box freeBox = boxesService.findNextFreeBox(department, departmentNumber, location);
        freeBox.setBoxStatus(Box.BoxStatus.OCCUPY);
        freeBox.setEmployee(employee);

        Set<Box> boxes = new HashSet<>();
        boxes.add(freeBox);
        employee.setBoxes(boxes);

        return employeesRepository.save(employee);
    }

    public void deleteEmployeeById(Long id) {
        Employee employeeById = employeesRepository.getEmployeeById(id);
        employeeById.getBoxes().stream().forEach(b -> boxesService.deleteEmployee(b));
        employeesRepository.deleteEmployeeById(id);
    }

    public Employee getEmployeeById(Long id) {
        return employeesRepository.getEmployeeById(id);
    }

    public List<Employee> getEmployeesByFirstNameAndLastNameSorted(String firstName, String lastName) {
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


    public Box changeEmployeeBox(Integer actualLockerNumber, Integer actualBoxNumber,
                                 Integer lockerNumber, Integer boxNumber,
                                 Locker.Location location, Locker.DepartmentNumber departmentNumber,
                                 Integer id) throws BoxNotAvailableException {
        Employee employee = getEmployeeById((long) id);
        Box newBox = boxesService.getBoxByParameters(lockerNumber, boxNumber, location, departmentNumber);
        if (newBox.getBoxStatus().equals(Box.BoxStatus.OCCUPY)) {
            throw new BoxNotAvailableException("Szafka o numerze: " + lockerNumber + "/" + boxNumber
                    + "jest niedostępna");
        } else {
            Box actualBox = employee.getBoxes().stream()
                    .filter(b -> b.getLocker().getLockerNumber().equals(actualLockerNumber))
                    .filter(b -> b.getBoxNumber().equals(actualBoxNumber))
                    .findFirst().get();
            boxesService.deleteEmployee(actualBox);

            newBox.setEmployee(employee);
            newBox.setBoxStatus(Box.BoxStatus.OCCUPY);

            return boxesService.saveBox(newBox);
        }
    }

    public Set<Box> dismissEmployeeById(Long id) {
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
}
