package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.repository.EmployeesRepository;
import pl.bratosz.smartlockers.repository.LockersRepository;
import pl.bratosz.smartlockers.service.BoxesService;
import pl.bratosz.smartlockers.service.EmployeeService;
import pl.bratosz.smartlockers.service.LockersService;


import javax.persistence.criteria.CriteriaBuilder;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lockers")
public class LockersController {

    private LockersRepository lockersRepository;
    private LockersService lockersService;
    private BoxesService boxesService;

    public LockersController(LockersRepository lockersRepository, LockersService lockersService, BoxesService boxesService) {
        this.lockersRepository = lockersRepository;
        this.lockersService = lockersService;
        this.boxesService = boxesService;
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping
    public List<Locker> getAll() {
        List<Locker> lockers = lockersRepository.findAll();
        if(lockers.size() < 50) {
            return lockers;
        } else {
        }
        return lockers.subList(0,50);
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/getLockersByDepartment")
    public List<Locker> getLockersByDepartment(Locker.DepartmentNumber depNo) {
        List<Locker> lockers = lockersRepository.findAllByDepartment(depNo);
        return lockers;
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/filter/{departmentNo}/{department}/{location}/{boxStatus}")
    public List<Locker> getFiltered(@PathVariable Locker.DepartmentNumber departmentNo,
                                    @PathVariable Department department,
                                    @PathVariable Locker.Location location,
                                    @PathVariable Box.BoxStatus boxStatus) {
        List<Locker> lockers = lockersRepository.filterAllByDepartmentNoAndDepartmentAndLocation(
                departmentNo, department, location);
        if (boxStatus.equals(Box.BoxStatus.UNDEFINED)) {
            return lockers.subList(0,50);
        }
        for (Locker locker : lockers) {
            List<Box> boxes = locker.getBoxes();
            List<Box> filteredBoxes = boxes.stream()
                    .filter(box -> box.getBoxStatus().equals(boxStatus))
                    .collect(Collectors.toList());
            locker.setBoxes(filteredBoxes);
        }
        return lockers.subList(0,50);
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
    @JsonView(Views.InternalForLockers.class)
    public Locker create(@RequestBody Locker locker) {
        List<Box> boxes = boxesService.createBoxesForLocker(locker.getCapacity());
        locker.setBoxes(boxes);
        return lockersRepository.save(locker);
    }

    @PostMapping("/create/{lockerNo}/{capacity}/{depNo}/{dep}/{location}")
        public Locker createLocker (
        @PathVariable int lockerNo,
        @PathVariable int capacity,
        @PathVariable Locker.DepartmentNumber depNo,
        @PathVariable Department dep,
        @PathVariable Locker.Location location) {
        return lockersService.createLocker(
                lockerNo, capacity, depNo, dep, location);
    }

    @JsonView(Views.InternalForLockers.class)
    @PostMapping("/change_location/{lockerNumber}/{departmentNumber}/{location}/{desiredLocation}")
    public Locker changeLocation(@PathVariable Integer lockerNumber, @PathVariable Locker.DepartmentNumber departmentNumber,
                                 @PathVariable Locker.Location location, @PathVariable Locker.Location desiredLocation) {
        return lockersService.changeLocation(lockerNumber, departmentNumber, location, desiredLocation);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        lockersRepository.deleteById(id);
    }

    @DeleteMapping("/deleteLockerById/{id}")
    public Locker deleteLockerByNumber(@PathVariable Long id) {
        return lockersService.deleteLockerByNumber(id);
    }
}
