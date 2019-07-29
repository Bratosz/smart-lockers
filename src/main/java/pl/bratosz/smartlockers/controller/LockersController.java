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

@RestController
@RequestMapping("/lockers")
public class LockersController {

    private LockersRepository lockersRepository;
    private BoxesRepository boxesRepository;
    private EmployeesRepository employeesRepository;

    public LockersController(LockersRepository lockersRepository, BoxesRepository boxesRepository, EmployeesRepository employeesRepository) {
        this.lockersRepository = lockersRepository;
        this.boxesRepository = boxesRepository;
        this.employeesRepository = employeesRepository;
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping
    public List<Locker> getAll() {
        return lockersRepository.findAll();
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/filter/{departmentNo}/{department}/{location}")
    public List<Locker> getFiltered(@PathVariable Locker.DepartmentNumber departmentNo,
                                    @PathVariable Department department,
                                    @PathVariable Locker.Location location) {
        return lockersRepository.filterAllByDepartmentNoAndDepartmentAndLocation(departmentNo, department, location);
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
            Employee employee = new Employee("", "", null);
            Box box = new Box(i, Box.BoxStatus.FREE);
            box.setEmployee(employee);
            boxes.add(box);
        }
        locker.setBoxes(boxes);
        return lockersRepository.save(locker);
    }

//    for(int i = 1; i <= 5; i++ ) {
//            List<Box> boxes = new LinkedList<>();
//            for(int j = 1; j <= 5; j++) {
//                Employee employee = new Employee("Jan " + j, "Nowak", Department.METAL);
//                employeesRepository.save(employee);
//
//                Box box = new Box(j, Box.BoxStatus.OCCUPY);
//                box.setEmployee(employee);
//                boxes.add(box);
//                boxesRepository.save(box);
//            }
//            Locker locker = new Locker(i, Department.METAL, Locker.DepartmentNumber.DEP_384, Locker.Location.OLDSIDE);
//            locker.setBoxes(boxes);
//            lockersRepository.save(locker);
//        }

    @DeleteMapping("/{id}/")
    public void delete(@PathVariable long id) {
        lockersRepository.deleteById(id);
    }
}
