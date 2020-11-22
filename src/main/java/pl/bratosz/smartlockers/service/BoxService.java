package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.BoxNotAvailableException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.service.creators.BoxCreator;

import java.util.LinkedList;
import java.util.List;

import static pl.bratosz.smartlockers.model.Box.BoxStatus.*;

@Service
public class BoxService {

    private BoxesRepository boxesRepository;

    public BoxService(BoxesRepository boxesRepository) {
        this.boxesRepository = boxesRepository;
    }

    public Box findNextFreeBox(Department department, int plantNumber, Location location) throws BoxNotAvailableException {
        Box.BoxStatus boxStatus = FREE;
        List<Box> boxes = boxesRepository.getBoxesByParameters(department, plantNumber, location, boxStatus);
        if (boxes.size() == 0) {
            throw new BoxNotAvailableException();
        }
        return boxes.stream().findFirst().get();
    }

    public Box dismissEmployee(Box box, EmployeeGeneral empToDismiss) {
        if (box.getBoxStatus().equals(OCCUPY)) {
            EmployeeDummy empForEmptyBox = box.getEmployeeDummy();
            if (empToDismiss.getClass().isInstance(EmployeeDummy.class)) {
                return box;
            } else {
                box.getDismissedEmployees().add((Employee) empToDismiss);
                box.setEmployee(empForEmptyBox);
                return boxesRepository.save(box);
            }
        } else {
            return box;
        }
    }


    public Box setEmployee(Employee employee, Box box) {
        box.setEmployee(employee);
        box.setBoxStatus(OCCUPY);
        return boxesRepository.save(box);
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

    public boolean isBoxFree(int lockerNo, int boxNo, Location location, int plantNumber) {
        Box box = boxesRepository.getBox(lockerNo, boxNo, location, plantNumber);
        if (box.getBoxStatus().equals(FREE)) {
            return true;
        } else {
            return false;
        }
    }

    public Box saveBox(Box box) {
        return boxesRepository.save(box);
    }

    public Box getBoxById(Long id) {
        return boxesRepository.getBoxById(id);
    }

    public List<Box> createBoxesForLocker(Locker locker) {
        BoxCreator boxCreator = new BoxCreator();
        List<Box> boxes = boxCreator.createBoxesForLocker(locker);
        return boxes;
    }

    public List<Box> getBoxesByLockersRange(int plantNumber, int firstLocker, int lastLocker) {
        return boxesRepository.getBoxesByLockersRange(plantNumber, firstLocker, lastLocker);
    }

    public List<Box> setActualBoxStatus() {
        List<Box> boxes = boxesRepository.findAll();
        List<Box> updatedBoxes = new LinkedList<>();

        for(Box b : boxes) {
            if(isEmployeePresent(b) && b.getBoxStatus().equals(FREE)) {
                b.setBoxStatus(OCCUPY);
                updatedBoxes.add(b);
                boxesRepository.save(b);
            } else if (!isEmployeePresent(b) && b.getBoxStatus().equals(OCCUPY)) {
                b.setBoxStatus(FREE);
                updatedBoxes.add(b);
                boxesRepository.save(b);
            }
        }
        return updatedBoxes;
    }

    private boolean isEmployeePresent(Box b) {
        EmployeeGeneral e = b.getEmployee();
        if(e.getClass().isInstance(Employee.class)){
            return true;
        } else {
            return false;
        }
    }


    public Employee getEmployeeToChangeBox(Box b) {
        EmployeeGeneral e = b.getEmployee();
        if(e.getClass().isInstance(Employee.class)){
            return (Employee) dismissEmployee(b, e).getEmployee();
        } else {
            throw new BoxNotAvailableException("Box is empty\n" + b.toString());
        }

    }

    public List<Box> getFiltered(long plantId, long departmentId, long locationId, Box.BoxStatus boxStatus) {
        List<Box> filtered = boxesRepository.getFiltered(plantId, departmentId, locationId, boxStatus);
        return filtered;
    }
}

