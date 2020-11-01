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
    @PostMapping("/create_employee/{plantId}/{departmentId}/{lockerNumber}/{boxNumber}/{firstName}/{lastName}")
    public ResponseEntity<String> createEmployee(@PathVariable long plantId,
                                                 @PathVariable long departmentId,
                                                 @PathVariable int lockerNumber,
                                                 @PathVariable int boxNumber,
                                                 @PathVariable String firstName,
                                                 @PathVariable String lastName) throws RuntimeException {
        firstName = firstName.toUpperCase();
        lastName = lastName.toUpperCase();
        employeeService.createEmployee(plantId, departmentId, lockerNumber, boxNumber, firstName, lastName);
        return ResponseEntity.ok("Employee added successfully!");

    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("add/{plantNumber}/{department}/{location}")
    public Employee createEmployee(@PathVariable int plantNumber,
                                   @PathVariable Department department,
                                   @PathVariable Location location,
                                   @RequestBody Employee employee) {
        return employeeService.createEmployeeAndAssignToBox(plantNumber, department, location, employee);
    }

    @JsonView(Views.Public.class)
    @DeleteMapping("/{id}")
    public void deleteFromBoxEmployeeById(@PathVariable Long id) {
        employeeService.dismissById(id);
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/find/{firstName}/{lastName}")
    public List<Employee> getByFirstNameAndLastName(@PathVariable String firstName, @PathVariable String lastName) {
        return employeeService.getByFirstNameAndLastName(firstName, lastName);
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/find/{lastName}")
    public List<Employee> getEmployeesByLastName(@PathVariable String lastName) {
        lastName.toUpperCase().trim();
        return employeeService.getEmployeesByLastName(lastName);
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/find_by_first_Name/{firstName}")
    public List<Employee> getEmployeesByFirstName(@PathVariable String firstName) {
        firstName.toUpperCase().trim();
        return employeeService.getEmployeesByFirstName(firstName);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/change_box/{lockerNumber}/{boxNumber}/{plantNumber}/{targetLockerNumber}/{targetBoxNumber}/{targetPlantNumber}")
    public Box changeEmployeeBox(@PathVariable int lockerNumber, @PathVariable int boxNumber,
                                 @PathVariable int plantNumber, @PathVariable int targetLockerNumber,
                                 @PathVariable int targetBoxNumber, @PathVariable int targetPlantNumber) {
        return employeeService.changeEmployeeBox(lockerNumber, boxNumber, plantNumber, targetLockerNumber,
                targetBoxNumber, targetPlantNumber);

    }

    @JsonView(Views.InternalForBoxes.class)
    @PostMapping("/change_box_next/{lockerNumber}/{boxNumber}/{plantNumber}/{targetDep}/{targetLocation}/{targetPlantNumber}")
    public Box changeEmployeeBoxOnNextAvaliable(
            @PathVariable int lockerNumber, @PathVariable int boxNumber,
            @PathVariable int plantNumber, @PathVariable Department targetDep,
            @PathVariable Location targetLocation, @PathVariable int targetPlantNumber) {
        return employeeService.changeEmployeeBoxOnNextFree(lockerNumber, boxNumber, plantNumber, targetDep,
                targetLocation, targetPlantNumber);
    }

    public List<Employee> sortByPlantLockerAndBox(List<Employee> employees) {
        return employeeService.sortByPlantBoxAndLocker(employees);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/dismiss_by_id/{id}")
    public Box dismissById(@PathVariable Long id) {
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
