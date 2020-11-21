package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.LockersRepository;
import pl.bratosz.smartlockers.service.BoxService;
import pl.bratosz.smartlockers.service.LockerService;


import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lockers")
public class LockerController {

    private LockersRepository lockersRepository;
    private LockerService lockerService;
    private BoxService boxesService;

    public LockerController(LockersRepository lockersRepository, LockerService lockerService, BoxService boxesService) {
        this.lockersRepository = lockersRepository;
        this.lockerService = lockerService;
        this.boxesService = boxesService;
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/{clientId}")
    public List<Locker> getAll(@PathVariable long clientId) {
        List<Locker> lockers = lockersRepository.getAllByClientId(clientId);
        if (lockers.size() < 2) {
            return lockers;
        } else {
        }
        return lockers.subList(0, 1);
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/filter/{plantId}/{lockerNumber}")
    public List<Locker> getLockersByPlantAndNumber(
            @PathVariable long plantId, @PathVariable int lockerNumber)
    {
        return lockerService.getLockersByPlantAndNumber(plantId, lockerNumber);
    }

    @GetMapping("/quantity/{plantId}")
    public int getLockersQuantity(@PathVariable long plantId) {
        return lockerService.getAmountOfLockersByPlantId(plantId);
    }

    @PostMapping("create/{plantId}/{departmentId}/{locationId}")
    @JsonView(Views.InternalForLockers.class)
    public Locker create(@PathVariable long plantId, @PathVariable long departmentId,
                         @PathVariable long locationId, @RequestBody Locker locker) {
        return lockerService.create(locker, plantId, departmentId, locationId);
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/filter/{plantId}/{departmentId}/{locationId}/{boxStatus}")
    public List<Box> getFiltered(@PathVariable long plantId,
                                    @PathVariable long departmentId,
                                    @PathVariable long locationId,
                                    @PathVariable Box.BoxStatus boxStatus) {
        List<Locker> lockers = lockersRepository.filterAllByPlantAndDepartmentAndLocation(
                plantId, departmentId, locationId);
        List<Box> filteredBoxes = new LinkedList<>();
        for (Locker locker : lockers) {
            List<Box> boxes = locker.getBoxes();
            for (Box b : boxes) {
                if(b.getBoxStatus().equals(boxStatus)){
                    filteredBoxes.add(b);
                }
            }
            if(filteredBoxes.size() > 50) {

                return filteredBoxes;
            }
        }
        return filteredBoxes;
    }

    @PostMapping("/create/{lockerNumber}/{capacity}/{plantNumber}/{department}/{location}")
    public Locker createLocker(
            @PathVariable int lockerNumber,
            @PathVariable int capacity,
            @PathVariable int plantNumber,
            @PathVariable String department,
            @PathVariable String location) {
        return lockerService.createLocker(
                lockerNumber, capacity, plantNumber, department, location);
    }

    @JsonView(Views.InternalForLockers.class)
    @PostMapping("/change_location/{lockerNumber}/{plantNumber}/{location}/{desiredLocation}")
    public Locker changeLocation(@PathVariable Integer lockerNumber, @PathVariable int plantNumber,
                                 @PathVariable Location location, @PathVariable Location desiredLocation) {
        return lockerService.changeLocation(lockerNumber, plantNumber, location, desiredLocation);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        lockersRepository.deleteById(id);
    }

    @DeleteMapping("/deleteLockerById/{id}")
    public Locker deleteLockerByNumber(@PathVariable Long id) {
        return lockerService.deleteLockerByNumber(id);
    }
}
