package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.repository.EmployeesRepository;
import pl.bratosz.smartlockers.repository.LockersRepository;


import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lockers")
public class LockersController {

    private LockersRepository lockersRepository;
    private EmployeesRepository employeesRepository;

    public LockersController(LockersRepository lockersRepository, EmployeesRepository employeesRepository) {
        this.lockersRepository = lockersRepository;
        this.employeesRepository = employeesRepository;
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping
    public List<Locker> getAll() {
        return lockersRepository.findAll();
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/filter/{departmentNo}/{department}/{location}/{boxStatus}")
    public List<Locker> getFiltered(@PathVariable Locker.DepartmentNumber departmentNo,
                                    @PathVariable Department department,
                                    @PathVariable Locker.Location location,
                                    @PathVariable Box.BoxStatus boxStatus) {
        List<Locker> lockers = lockersRepository.filterAllByDepartmentNoAndDepartmentAndLocation(
                departmentNo, department, location);
        if(boxStatus.equals(Box.BoxStatus.UNDEFINED)){
            return lockers;
        }
            for (Locker locker : lockers) {
                List<Box> boxes = locker.getBoxes();
                List<Box> filteredBoxes = boxes.stream()
                        .filter(box -> box.getBoxStatus().equals(boxStatus))
                        .collect(Collectors.toList());
                locker.setBoxes(filteredBoxes);
            }
            return lockers;
    }


    @GetMapping("/quantity/{departmentNo}")
    public int getLockersQuantity(@PathVariable Locker.DepartmentNumber departmentNo) {
        return lockersRepository.getAmountOfLockersByDepartmentNumber(departmentNo);
    }

//    @RequestMapping(
//            value = "/ex/bars",
//            params = { "id", "second" },
//            method = GET)

//    (@PathVariable long fooid, @PathVariable long barid)

    @PostMapping
    public Locker create(@RequestBody Locker locker) {


        List<Box> boxes = new LinkedList<>();
        for (int i = 1; i <= locker.getCapacity(); i++) {
            Employee employee = employeesRepository.save(new Employee("", "", null));
            List<Employee> employees = new LinkedList<>();
            employees.add(employee);
            Box box = new Box(i, Box.BoxStatus.FREE, employee.getId());
            box.setEmployee(employee);
            box.setDismissedEmployees(employees);
            boxes.add(box);
        }
        locker.setBoxes(boxes);
        return lockersRepository.save(locker);
    }

    @DeleteMapping("/{id}/")
    public void delete(@PathVariable long id) {
        lockersRepository.deleteById(id);
    }
}
