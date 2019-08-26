package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.repository.EmployeesRepository;
import pl.bratosz.smartlockers.repository.LockersRepository;
import pl.bratosz.smartlockers.service.EmployeeService;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("{id}")
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
                                   @PathVariable Locker.DepartmentNumber departmentNumber,
                                   @PathVariable Locker.Location location,
                                   @RequestBody Employee employee) {
        return employeeService.createEmployeeAndAssignToBox(department, departmentNumber, location, employee);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteById(id);
    }

}
