package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.LockersRepository;
import pl.bratosz.smartlockers.service.managers.creators.LockerCreator;

import java.util.LinkedList;
import java.util.List;

import static pl.bratosz.smartlockers.model.Box.*;
import static pl.bratosz.smartlockers.model.Box.BoxStatus.ALL;

@Service
public class LockerService {
    private LockersRepository lockersRepository;
    private EmployeeService employeeService;
    private BoxService boxesService;
    private PlantService plantService;
    private DepartmentService departmentService;
    private LocationService locationService;

    public LockerService(LockersRepository lockersRepository, EmployeeService employeeService,
                         BoxService boxesService, PlantService plantService, DepartmentService departmentService,
                         LocationService locationService) {
        this.lockersRepository = lockersRepository;
        this.employeeService = employeeService;
        this.boxesService = boxesService;
        this.plantService = plantService;
        this.departmentService = departmentService;
        this.locationService = locationService;
    }

    public Locker deleteLockerByNumber(Long id) {
        return lockersRepository.deleteLockerById(id);
    }

    public Locker changeLocation(Integer lockerNumber, int plantNumber,
                                 Location location, Location desiredLocation) {
        Locker locker = getLockerByParameters(lockerNumber, plantNumber, location);
        locker.setLocation(desiredLocation);
        return lockersRepository.save(locker);
    }

    public Locker getLockerByParameters(Integer lockerNumber,
                                        int plantNumber,
                                        Location location) {
        return lockersRepository.getLockerByParameters(lockerNumber, plantNumber, location);
    }

    public List<Locker> getLockersFromRange(int plantNumber, int firstLocker, int lastLocker) {
        return lockersRepository.getLockersFromRange(plantNumber, firstLocker, lastLocker);
    }

    public Locker createLocker(
            int lockerNumber,
            int capacity,
            int plantNumber,
            String departmentName,
            String locationName) {
        Plant plant = plantService.getByNumber(plantNumber);
        Department department = departmentService.getByNameAndPlantNumber(departmentName, plantNumber);
        Location location = locationService.getByNameAndPlantNumber(locationName, plantNumber);

        Locker locker = LockerCreator.create(
                lockerNumber, capacity, plant, department, location);
        return lockersRepository.save(locker);
    }

    public List<Locker> create(
            int startingLockerNumber,
            int endingLockerNumber,
            int capacity,
            long plantId,
            long departmentId,
            long locationId) {
        Plant plant = plantService.getById(plantId);
        Department department = departmentService.getById(departmentId);
        Location location = locationService.getById(locationId);
        List<Locker> lockers = new LinkedList<>();
        for(int i = startingLockerNumber; i <= endingLockerNumber; i++) {
            lockers.add(
                    LockerCreator.create(
                    i, capacity, plant, department, location));
        }
        return lockersRepository.saveAll(lockers);
    }

    public Locker create(Locker locker) {
        return lockersRepository.save(locker);
    }

    public List<Locker> getLockersByPlantNumber(int plantNumber) {
        List<Locker> lockers = lockersRepository.findAllByPlantNumber(plantNumber);
        return lockers;
    }

    public int getAmountOfLockersByPlantId(long id) {
        return lockersRepository.getAmountOfLockersByPlantId(id);
    }

    public void saveLockers(List<Locker> lockers) {
        lockersRepository.saveAll(lockers);
    }

    public List<Locker> getLockersByPlantAndNumber(
            long plantId,
            int lockerNumber) {
        return lockersRepository.getLockersByPlantAndNumber(plantId, lockerNumber);
    }

    public List<Locker> getFiltered(
            long plantId,
            long departmentId,
            long locationId) {
        Plant plant = plantService.getById(plantId);
        Department department = departmentService.getById(departmentId);
        Location location = locationService.getById(locationId);
        return lockersRepository.getFiltered(
                plant, department, location);
    }

    public List<Locker> getFiltered(
            long plantId,
            long departmentId,
            long locationId,
            BoxStatus boxStatus) {
        List<Locker> lockers = getFiltered(
                plantId, departmentId, locationId);
        for (Locker locker : lockers) {
            List<Box> boxes = locker.getBoxes();
            List<Box> filteredBoxes = new LinkedList<>();
            for (Box b : boxes) {
                if(boxStatus.equals(ALL) || b.getBoxStatus().equals(boxStatus)) {
                    filteredBoxes.add(b);
                }
            }
            locker.setBoxes(filteredBoxes);
        }
        if(lockers.size() <= 50) {
            return lockers;
        } else {
            return lockers.subList(0, 49);
        }
    }

    public Locker getLockerById(long lockerId) {
        return lockersRepository.getLockerById(lockerId);
    }
}
