package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.exception.WrongIdException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.service.EmployeeService;

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
    @PostMapping("/create_employee/{plantNumber}/{lockerNumber}/{boxNumber}")
    public ResponseEntity<String> createEmployee(@PathVariable int plantNumber,
                                                 @PathVariable int lockerNumber,
                                                 @PathVariable int boxNumber,
                                                 @RequestBody Employee employee) throws RuntimeException {
        employeeService.createEmployee(plantNumber, lockerNumber, boxNumber, employee);
        return ResponseEntity.ok("Employee added successfully!");

    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("add/{department}/{plantNumber}/{location}")
    public Employee createEmployee(@PathVariable Department department,
                                   @PathVariable Locker.Location location,
                                   @RequestBody Employee employee) {
        return employeeService.createEmployeeAndAssignToBox(department, location, employee);
    }

    @JsonView(Views.Public.class)
    @DeleteMapping("/{id}")
    public void deleteFromBoxEmployeeById(@PathVariable Long id) {
        employeeService.deleteFromBoxEmployeeById(id);
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/find/{firstName}/{lastName}")
    public List<Employee> getEmployeesByFirstNameAndLastName(@PathVariable String firstName, @PathVariable String lastName) {
        List<Employee> employeesByFirstNameAndLastName = employeeService.getByFirstNameAndLastName(firstName, lastName);
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
    public Set<Box> dismissById(@PathVariable Long id) {
        return employeeService.dismissById(id);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/change_last_name_by_id/{id}")
    public Employee changeEmployeeLastNameById(
            @RequestBody Employee employee, @PathVariable Long id) {
        return employeeService.changeEmployeeLastName(employee.getLastName(), id);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/change_first_name_by_id/{id}")
    public Employee changeEmployeeFirstNameById(
            @RequestBody Employee employee, @PathVariable Long id) {
        return employeeService.changeEmployeeFirstNameById(employee.getFirstName(), id);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/change_first_name_and_last_name_by_id/{id}")
    public Employee changeEmployeeFirstAndLastNameById(
            @RequestBody Employee employee, @PathVariable Long id) {
        return employeeService.changeEmployeeFirstNameAndLastNameById(employee, id);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/change_department/{id}")
    public Employee changeDepartment(
            @RequestBody Employee employee, @PathVariable Long id) {
        return employeeService.changeDepartment(employee.getDepartment(), id);
    }
}
