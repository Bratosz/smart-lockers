package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.exception.InvalidEmployeeException;
import pl.bratosz.smartlockers.exception.WrongIdException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.service.EmployeeService;
import pl.bratosz.smartlockers.validators.EmployeeValidator;
import sun.security.provider.certpath.OCSPResponse;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) throws RuntimeException {
        if (id < 0) throw new WrongIdException("Passed id is: " + id + ". It should be higher or equal to 0");
        return employeeService.getEmployeeById(id);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/create_employee/{departmentNumber}/{lockerNumber}/{boxNumber}")
    public ResponseEntity<String> createEmployee(@PathVariable Locker.DepartmentNumber departmentNumber,
                                                 @PathVariable Integer lockerNumber,
                                                 @PathVariable Integer boxNumber,
                                                 @RequestBody Employee employee) throws RuntimeException {
        employeeService.createEmployee(departmentNumber, lockerNumber, boxNumber, employee);
        return ResponseEntity.ok("Employee added successfully!");

    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("add/{department}/{departmentNumber}/{location}")
    public Employee createEmployee(@PathVariable Department department,
                                   @PathVariable Locker.Location location,
                                   @RequestBody Employee employee) {
        return employeeService.createEmployeeAndAssignToBox(department, location, employee);
    }

    @JsonView(Views.Public.class)
    @DeleteMapping("/{id}")
    public void deleteEmployeeById(@PathVariable Long id) {
        employeeService.deleteEmployeeById(id);
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/find/{firstName}/{lastName}")
    public List<Employee> getEmployeesByFirstNameAndLastName(@PathVariable String firstName, @PathVariable String lastName) {
        List<Employee> employeesByFirstNameAndLastName = employeeService.getEmployeesByFirstNameAndLastNameSorted(firstName, lastName);
        return employeesByFirstNameAndLastName;
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/find/{lastName}")
    public List<Employee> getEmployeesByLastName(@PathVariable String lastName) {
        lastName.toUpperCase();
        return employeeService.getEmployeesByLastName(lastName);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/change_box/{lockerNumber}/{boxNumber}/{depNumber}/{targetLockerNumber}/{targetBoxNumber}/{targetDepNumber}")
    public Box changeEmployeeBox(@PathVariable int lockerNumber, @PathVariable int boxNumber,
                                 @PathVariable Locker.DepartmentNumber depNumber, @PathVariable int targetLockerNumber,
                                 @PathVariable int targetBoxNumber, @PathVariable Locker.DepartmentNumber targetDepNumber) {
        return employeeService.changeEmployeeBox(lockerNumber, boxNumber, depNumber, targetLockerNumber,
                targetBoxNumber, targetDepNumber);

    }

    @JsonView(Views.InternalForBoxes.class)
    @PostMapping("/change_box_next/{lockerNumber}/{boxNumber}/{depNumber}/{targetDep}/{targetLocation}/{targetDepNumber}")
    public Box changeEmployeeBoxOnNextAvaliable(@PathVariable int lockerNumber, @PathVariable int boxNumber,
                                                @PathVariable Locker.DepartmentNumber depNumber, @PathVariable Department targetDep,
                                                @PathVariable Locker.Location targetLocation, @PathVariable Locker.DepartmentNumber targetDepNumber) {
        return employeeService.changeEmployeeBoxOnNextFree(lockerNumber, boxNumber, depNumber, targetDep,
                targetLocation, targetDepNumber);
    }

    public List<Employee> sortEmployeesByDepartmentLockerAndBox(List<Employee> employees) {
        return employeeService.sortEmployeesByDepartmentBoxAndLocker(employees);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/dismiss_by_id/{id}")
    public Set<Box> dismissEmployeeById(@PathVariable Long id) {
        return employeeService.dismissEmployeeById(id);
    }

    @PostMapping("/change_last_name_by_id/{lastName}/{id}")
    public Employee changeEmployeeLastNameById(@PathVariable String lastName, @PathVariable Long id) {
        return employeeService.changeEmployeeLastName(lastName, id);
    }

}
