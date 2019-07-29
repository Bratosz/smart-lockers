package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.repository.EmployeesRepository;
import pl.bratosz.smartlockers.repository.LockersRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private EmployeesRepository employeesRepository;
    private BoxesRepository boxesRepository;
    private LockersRepository lockersRepository;

    public EmployeeController(EmployeesRepository employeesRepository, BoxesRepository boxesRepository, LockersRepository lockersRepository) {
        this.employeesRepository = employeesRepository;
        this.boxesRepository = boxesRepository;
        this.lockersRepository = lockersRepository;
    }

    @GetMapping
    public List<Employee> getAll() {
        return employeesRepository.findAll();
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/{departmentNumber}/{lockerNumber}/{boxNumber}")
    public Employee createEmployee(@PathVariable Locker.DepartmentNumber departmentNumber,
                              @PathVariable Integer lockerNumber,
                              @PathVariable Integer boxNumber,
                              @RequestBody Employee employee) {
        //saving box with proper status
        Box box = lockersRepository.getBox(departmentNumber, lockerNumber, boxNumber);
        employeesRepository.save(employee);
        box.setEmployee(employee);
        box.setBoxStatus(Box.BoxStatus.OCCUPY);
        boxesRepository.save(box);

        //adding adding box to employee
        Set<Box> boxes = new HashSet<>();
        boxes.add(box);
        employee.setBoxes(boxes);
        return employeesRepository.save(employee);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeesRepository.deleteById(id);
    }

}
