package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.LockersRepository;
import pl.bratosz.smartlockers.service.BoxService;
import pl.bratosz.smartlockers.service.LockerService;


import java.util.List;

@RestController
@RequestMapping("/lockers")
public class LockerController {

    private LockersRepository lockersRepository;
    private LockerService lockersService;
    private BoxService boxesService;

    public LockerController(LockersRepository lockersRepository, LockerService lockersService, BoxService boxesService) {
        this.lockersRepository = lockersRepository;
        this.lockersService = lockersService;
        this.boxesService = boxesService;
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/and_boxes/{clientId}")
    public List<Locker> getAllWithBoxes(@PathVariable long clientId) {
        List<Locker> lockers = lockersRepository.getAllByClientId(clientId);
        if (lockers.size() <= 3) {
            return lockers;
        } else {
        }
        return lockers.subList(0, 2);
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/{clientId}")
    public List<Locker> getAll(
            @PathVariable long clientId) {
        return lockersRepository.getAllByClientId(clientId);
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/filter/{plantId}/{lockerNumber}")
    public List<Locker> getLockersByPlantAndNumber(
            @PathVariable long plantId,
            @PathVariable int lockerNumber) {
        return lockersService.getLockersByPlantAndNumber(plantId, lockerNumber);
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/get/{lockerId}")
    public Locker getBy(@PathVariable long lockerId) {
        return lockersService.getBy(lockerId);
    }

    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/get-boxes/{lockerId}")
    public List<Box> getBoxesBy(@PathVariable long lockerId) {
        return lockersService.getBoxesBy(lockerId);
    }

    @JsonView(Views.Public.class)
    @GetMapping("/filter/{plantId}/{departmentId}/{locationId}")
    public List<Locker> getFiltered(
            @PathVariable long plantId,
            @PathVariable long departmentId,
            @PathVariable long locationId) {
        return lockersService.getFiltered(
                plantId,
                departmentId,
                locationId);
    }

    @PostMapping("/createDefault" +
            "/{startingLockerNumber}" +
            "/{endingLockerNumber}" +
            "/{capacity}" +
            "/{plantId}" +
            "/{departmentId}" +
            "/{locationId}")
    @JsonView(Views.InternalForLockers.class)
    public List<Locker> create(
            @PathVariable int startingLockerNumber,
            @PathVariable int endingLockerNumber,
            @PathVariable int capacity,
            @PathVariable long plantId,
            @PathVariable long departmentId,
            @PathVariable long locationId) {
        return lockersService.create(
                startingLockerNumber,
                endingLockerNumber,
                capacity,
                plantId,
                departmentId,
                locationId);
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/filter/{plantId}/{departmentId}/{locationId}/{boxStatus}")
    public List<Locker> getFiltered(@PathVariable long plantId,
                                 @PathVariable long departmentId,
                                 @PathVariable long locationId,
                                 @PathVariable Box.BoxStatus boxStatus) {
        return lockersService.getFiltered(plantId, departmentId, locationId, boxStatus);
    }

    @PostMapping("/create/{lockerNumber}/{capacity}/{plantNumber}/{department}/{location}")
    public Locker createLocker(
            @PathVariable int lockerNumber,
            @PathVariable int capacity,
            @PathVariable int plantNumber,
            @PathVariable String department,
            @PathVariable String location) {
        return lockersService.create(
                lockerNumber, capacity, plantNumber, department, location);
    }

    @JsonView(Views.InternalForLockers.class)
    @PostMapping("/change_location/{lockerNumber}/{plantNumber}/{location}/{desiredLocation}")
    public Locker changeLocation(@PathVariable Integer lockerNumber, @PathVariable int plantNumber,
                                 @PathVariable Location location, @PathVariable Location desiredLocation) {
        return lockersService.changeLocation(lockerNumber, plantNumber, location, desiredLocation);
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
