package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.BoxNotAvailableException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.service.managers.creators.BoxCreator;
import pl.bratosz.smartlockers.service.managers.BoxManager;

import java.util.LinkedList;
import java.util.List;

import static pl.bratosz.smartlockers.model.Box.BoxStatus.*;

@Service
public class BoxService {

    private BoxesRepository boxesRepository;
    private UserService userService;
    private User user;
    private BoxManager boxManager;
    private EmployeeService employeeService;

    public BoxService(BoxesRepository boxesRepository) {
        this.boxesRepository = boxesRepository;
    }

    public void loadUserAndManager(long userId) {
        user = userService.getUserById(userId);
        boxManager = new BoxManager(this.user);
    }

    public void loadUserAndManager(User user) {
        this.user = user;
        boxManager = new BoxManager(this.user);
    }

    public Box findNextFreeBox(Department department, int plantNumber, Location location) throws BoxNotAvailableException {
        Box.BoxStatus boxStatus = FREE;
        List<Box> boxes = boxesRepository.getBoxesByParameters(department, plantNumber, location, boxStatus);
        if (boxes.size() == 0) {
            throw new BoxNotAvailableException();
        }
        return boxes.stream().findFirst().get();
    }

    public Box releaseBoxAndDismissEmployee(long boxId, long userId) {
        user = userService.getUserById(userId);
        Box box = boxesRepository.getBoxById(boxId);

        box = boxManager.release(box);
        return boxesRepository.save(box);
    }


    public Box setEmployee(EmployeeGeneral employee, Box box) {
        if(employee.getClass().isInstance(Employee.class)) {
            box.setEmployee(employee);
            box.setBoxStatus(OCCUPY);
            return boxesRepository.save(box);
        } else {
            return box;
        }
    }

    public Box changeEmployeeBoxOnNextFree(int lockerNumber, int boxNumber,
                                           long plantId, Department targetDep,
                                           Location targetLocation, int targetPlantNumber, long userId) {
        user = userService.getUserById(userId);
        loadUserAndManager(user);
        Box oldBox = getBox(plantId, lockerNumber, boxNumber);
        Box freeBox = findNextFreeBox(targetDep, targetPlantNumber, targetLocation);
        EmployeeGeneral employee = extractEmployee(oldBox);
        return setEmployee(employee, freeBox);
    }

    public Box changeEmployeeBox(long userId, int lockerNumber, int boxNumber, long plantId,
                                 int targetLockerNumber, int targetBoxNumber,
                                 long targetPlantId) throws BoxNotAvailableException {
        User user = userService.getUserById(userId);
        loadUserAndManager(user);
        Box newBox = getBox(targetPlantId, targetLockerNumber, targetBoxNumber);

        if (newBox.getBoxStatus().equals(OCCUPY)) {
            throw new BoxNotAvailableException("Szafka o numerze: " + targetLockerNumber + "/" + targetBoxNumber
                    + "jest niedostępna");
        } else {
            Box oldBox = getBox(plantId, lockerNumber, boxNumber);
            EmployeeGeneral employee = extractEmployee(oldBox);
            return setEmployee(employee, newBox);
        }
    }

    public List<Box> findAll() {
        return boxesRepository.findAll();
    }

    public Box getBox(int lockerNumber, int boxNumber, Location location,
                      int plantNumber) {
        return boxesRepository.getBox(lockerNumber, boxNumber, location, plantNumber);
    }

    public Box getBox(long plantId, int lockerNumber, int boxNumber) {
        return boxesRepository.getBox(plantId, lockerNumber, boxNumber);
    }

    public Box getBoxById(Long id) {
        return boxesRepository.getBoxById(id);
    }

    public List<Box> createBoxesForLocker(Locker locker) {
        BoxCreator boxCreator = new BoxCreator();
        List<Box> boxes = boxCreator.createBoxesForLocker(locker);
        return boxes;
    }

    public List<Box> setActualBoxStatus() {
        List<Box> boxes = boxesRepository.findAll();
        List<Box> updatedBoxes = new LinkedList<>();

        for (Box b : boxes) {
            if (employeeIsPresent(b) && b.getBoxStatus().equals(FREE)) {
                b.setBoxStatus(OCCUPY);
                updatedBoxes.add(b);
                boxesRepository.save(b);
            } else if (!employeeIsPresent(b) && b.getBoxStatus().equals(OCCUPY)) {
                b.setBoxStatus(FREE);
                updatedBoxes.add(b);
                boxesRepository.save(b);
            }
        }
        return updatedBoxes;
    }

    private boolean employeeIsPresent(Box b) {
        EmployeeGeneral e = b.getEmployee();
        return employeeIsPresent(e);
    }

    private boolean employeeIsPresent(EmployeeGeneral e) {
        if (e.getClass().isInstance(Employee.class))
            return true;
        return false;

    }

    public List<Box> getFiltered(long plantId, long departmentId, long locationId, Box.BoxStatus boxStatus) {
        List<Box> filtered = boxesRepository.getFiltered(plantId, departmentId, locationId, boxStatus);
        return filtered;
    }

    public Employee extractEmployee(Box box) {
        Box releasedBox = boxManager.release(box);
        updateBox(releasedBox);
        List<Employee> releasedEmployees = releasedBox.getReleasedEmployees();
        return releasedEmployees.get(releasedEmployees.size() - 1);
    }

    private void updateBox(Box box) {
        boxesRepository.save(box);
    }


}

