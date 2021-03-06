package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.BoxNotAvailableException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.LockersRepository;
import pl.bratosz.smartlockers.service.managers.creators.LockerCreator;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Locker> getLockers(int from, int to, long plantId) {
        return lockersRepository.getLockers(from, to, plantId);
    }

    public Locker create(
            int lockerNumber,
            int capacity,
            int plantNumber,
            String departmentName,
            String locationName) {
        Plant plant = plantService.getByNumber(plantNumber);
        Department department = departmentService.getByNameAndPlantNumber(departmentName, plantNumber);
        Location location = locationService.getByNameAndPlantNumber(locationName, plantNumber);

        return create(lockerNumber, capacity, plant, department, location);
    }

    public Locker create(
            int lockerNumber,
            int capacity,
            Plant plant,
            Department department,
            Location location) {
        Locker locker = LockerCreator.create(
                lockerNumber, capacity, plant, department, location);
        return lockersRepository.save(locker);
    }


    public Locker createWithCustomBoxNumbers(
            int lockerNumber,
            List<Integer> boxNumbers,
            Location location,
            Department department,
            Plant plant) {
        Locker locker = LockerCreator.createWithCustomBoxNumbers(
                lockerNumber, boxNumbers, plant, department, location);
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
        for (int i = startingLockerNumber; i <= endingLockerNumber; i++) {
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

    public List<Locker> saveLockers(List<Locker> lockers) {
        return lockersRepository.saveAll(lockers);
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
                if (boxStatus.equals(ALL) || b.getBoxStatus().equals(boxStatus)) {
                    filteredBoxes.add(b);
                }
            }
            locker.setBoxes(filteredBoxes);
        }
        if (lockers.size() <= 50) {
            return lockers;
        } else {
            return lockers.subList(0, 49);
        }
    }

    public Locker getBy(long lockerId) {
        return lockersRepository.getLockerById(lockerId);
    }

    public Box getBoxByNumber(Locker locker, int boxNumber) {
        return locker.getBoxes()
                .stream()
                .filter(b -> b.getBoxNumber() == boxNumber)
                .findFirst().get();
    }


    public List<Box> getBoxesBy(long lockerId) {
        return lockersRepository.getLockerById(lockerId).getBoxes();
    }

    public List<Locker> changeDepartmentAndLocation(
            int startingLockerNumber,
            int endLockerNumber,
            long plantId,
            long departmentId,
            long locationId) {
        Department department = departmentService.getById(departmentId);
        Location location = locationService.getById(locationId);
        List<Locker> lockers = lockersRepository.getLockers(
                startingLockerNumber,
                endLockerNumber,
                plantId);

        lockers.stream()
                .forEach(l -> {
                    l.setDepartment(department);
                    l.setLocation(location);
                });
        return lockersRepository.saveAll(lockers);
    }

    public List<Locker> getAllBy(long plantId) {
        return lockersRepository.getAllByPlantId(plantId);
    }

    public Locker create(
            int lockerNumber,
            List<Box> boxes,
            int capacity,
            Plant plant,
            Department department,
            Location location) {
        Locker l = LockerCreator.create(
                lockerNumber,
                boxes,
                capacity,
                plant,
                department,
                location);
        return lockersRepository.save(l);
    }

    public void saveLocker(Locker l) {
        lockersRepository.save(l);
    }
}
