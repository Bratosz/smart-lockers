package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.comparators.BoxNumberSorter;
import pl.bratosz.smartlockers.comparators.DepartmentNumberSorter;
import pl.bratosz.smartlockers.comparators.LockerNumberSorter;
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

    public Employee createEmployee(Locker.DepartmentNumber departmentNumber,
                                   Integer lockerNumber, Integer boxNumber, Employee employee) {
        Box box = lockersRepository.getBox(departmentNumber, lockerNumber, boxNumber);
        box.setBoxStatus(Box.BoxStatus.OCCUPY);
        box.setEmployee(employee);
        return employeesRepository.save(employee);
    }

    public Employee createEmployee(Employee employee) {
        Set<Box> boxes = employee.getBoxes();
        boxes.forEach(b -> b.setBoxStatus(Box.BoxStatus.OCCUPY));
        return employeesRepository.save(employee);
    }

    public Employee createEmployeeAndAssignToBox(Department department, Locker.Location location, Employee employee) {
        Locker.DepartmentNumber departmentNumber;
        //chosing department number by location and department
        if(department.equals(Department.METAL) || (department.equals(Department.JIT) && location.equals(Locker.Location.OLDSIDE))) {
            departmentNumber = Locker.DepartmentNumber.DEP_384;
        } else {
            departmentNumber = Locker.DepartmentNumber.DEP_385;
        }

        //change location if there is no free boxes
        if(boxesService.findNextFreeBox(department, departmentNumber, location).equals(null)) {
            if(department.equals(Department.METAL)) {
                location = Locker.Location.NEWSIDE;
            } else if(department.equals(Department.JIT) && location.equals(Locker.Location.OLDSIDE)) {
                departmentNumber = Locker.DepartmentNumber.DEP_385;
                location = Locker.Location.NEWSIDEUPSTAIRS;
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

    public void deleteById(Long id) {
        employeesRepository.deleteById(id);
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
}
