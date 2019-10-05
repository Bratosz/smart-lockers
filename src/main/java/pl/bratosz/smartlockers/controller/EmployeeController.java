package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.service.EmployeeService;

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
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/{departmentNumber}/{lockerNumber}/{boxNumber}")
    public Employee createEmployee(@PathVariable Locker.DepartmentNumber departmentNumber,
                                         @PathVariable Integer lockerNumber,
                                         @PathVariable Integer boxNumber,
                                         @RequestBody Employee employee) {
        
        return employeeService.createEmployee(departmentNumber, lockerNumber, boxNumber, employee);

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
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteById(id);
    }

    @GetMapping("/find/{firstName}/{lastName}")
    public List<Employee> getEmployeesByFirstNameAndLastName(@PathVariable String firstName, @PathVariable String lastName) {
        List<Employee> employeesByFirstNameAndLastName = employeeService.getEmployeesByFirstNameAndLastNameSorted(firstName, lastName);
        return employeesByFirstNameAndLastName;
    }

    public List<Employee> sortEmployeesByDepartmentLockerAndBox(List<Employee> employees) {
        return employeeService.sortEmployeesByDepartmentBoxAndLocker(employees);
    }
}
