package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.exception.WrongIdException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.service.EmployeeService;
import pl.bratosz.smartlockers.strings.MyString;

import java.util.List;

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
    public Employee getEmployeeById(
            @PathVariable Long id) throws RuntimeException {
        if (id < 0) throw new WrongIdException("Passed id is: " + id + ". It should be higher or equal to 0");
        return employeeService.getById(id);
    }

    @JsonView(Views.EmployeeCompleteInfo.class)
    @GetMapping("/with-complete-info/{id}")
    public Employee getWithCompleteInfo(@PathVariable long id) {
        return employeeService.getById(id);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/create-employee-and-add-to-box/{boxId}/{departmentId}/{firstName}/{lastName}")
    public ResponseEntity<String> createEmployee(
            @PathVariable long boxId,
            @PathVariable long departmentId,
            @PathVariable String firstName,
            @PathVariable String lastName) throws RuntimeException {
        firstName = firstName.toUpperCase();
        lastName = lastName.toUpperCase();
        employeeService.createEmployee(boxId, departmentId, firstName, lastName);
        return ResponseEntity.ok("Dodano pracownika");

    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("add/{plantNumber}/{department}/{location}")
    public Employee createEmployee(@PathVariable int plantNumber,
                                   @PathVariable Department department,
                                   @PathVariable Location location,
                                   @RequestBody Employee employee) {
        return employeeService.createEmployeeAndAssignToBox(plantNumber, department, location, employee);
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/find/{firstName}/{lastName}")
    public List<Employee> getByFirstNameAndLastName(@PathVariable String firstName, @PathVariable String lastName) {
        return employeeService.getByFirstNameAndLastName(firstName, lastName);
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/find-by-last-name/{lastName}/{clientId}")
    public List<Employee> getEmployeesByLastName(
            @PathVariable String lastName,
            @PathVariable long clientId) {
         lastName = MyString.create(lastName).get();
        return employeeService.getEmployeesByLastName(lastName, clientId);
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/find_by_first_Name/{firstName}")
    public List<Employee> getEmployeesByFirstName(@PathVariable String firstName) {
        firstName.toUpperCase().trim();
        return employeeService.getEmployeesByFirstName(firstName);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/change_box/{userId}/{lockerNumber}/{boxNumber}/{plantNumber}/{targetLockerNumber}/{targetBoxNumber}/{targetPlantNumber}")
    public Box changeEmployeeBox(
            @PathVariable long userId,
            @PathVariable int lockerNumber,
            @PathVariable int boxNumber,
            @PathVariable int plantNumber,
            @PathVariable int targetLockerNumber,
            @PathVariable int targetBoxNumber,
            @PathVariable int targetPlantNumber) {
        return employeeService.changeEmployeeBox(userId, lockerNumber, boxNumber, plantNumber, targetLockerNumber,
                targetBoxNumber, targetPlantNumber);

    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/dismiss-by-id/{employeeId}/{userId}")
    public Employee dismissBy(
            @PathVariable long employeeId,
            @PathVariable long userId) {
        return employeeService.dismissBy(employeeId, userId);
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

    @PostMapping("/relocate/{plantId}/{departmentId}/{locationId}/{employeeId}")
    public String relocate(
            @PathVariable long plantId,
            @PathVariable long departmentId,
            @PathVariable long locationId,
            @PathVariable long employeeId) {
        return employeeService.relocate(plantId, departmentId, locationId, employeeId);
    }

}
