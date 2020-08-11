package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.BoxNotAvailableException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.repository.EmployeesRepository;

import java.util.LinkedList;
import java.util.List;

import static pl.bratosz.smartlockers.model.Box.BoxStatus.*;

@Service
public class BoxService {

    private BoxesRepository boxesRepository;
    private EmployeesRepository employeesRepository;
    private LockerService lockerService;


    public BoxService(BoxesRepository boxesRepository, EmployeesRepository employeesRepository) {
        this.boxesRepository = boxesRepository;
        this.employeesRepository = employeesRepository;
    }

    public Box findNextFreeBox(Department department, int plantNumber, Location location) throws BoxNotAvailableException {
        Box.BoxStatus boxStatus = FREE;
        List<Box> boxes = boxesRepository.getBoxesByParameters(department, plantNumber, location, boxStatus);
        if (boxes.size() == 0) {
            throw new BoxNotAvailableException();
        }
        return boxes.stream().findFirst().get();
    }

    public Box dismissEmployee(Box box) {
        box.getDismissedEmployees().add(box.getEmployee());
        box.setEmployee(employeesRepository.getEmployeeById(box.getEmptyBoxEmployeeNo()));
        box.setBoxStatus(FREE);
        return boxesRepository.save(box);
    }

    public Employee releaseEmployeeFromBox(Box box) {
        Employee employee = box.getEmployee();
        employee.getBoxesOccupiedInPast().add(box);
        box.setEmployee(employeesRepository.getEmployeeById(box.getEmptyBoxEmployeeNo()));
        box.setBoxStatus(FREE);
        boxesRepository.save(box);
        return employee;
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

    public List<Box> createBoxesForLocker(int lockerCapacity) {
        List<Box> boxes = new LinkedList<>();
        for (int i = 1; i <= lockerCapacity; i++) {
            Employee employee = new Employee("", "", null);
            List<Employee> employees = new LinkedList<>();
            employees.add(employee);

            Box box = new Box(i, FREE, employee.getId());
            box.setEmployee(employee);
            box.setDismissedEmployees(employees);
            boxes.add(box);
        }
        return boxes;
    }

    public List<Box> getBoxesByLockersRange(int plantNumber, int firstLocker, int lastLocker) {
        return boxesRepository.getBoxesByLockersRange(plantNumber, firstLocker, lastLocker);
    }

    public Box setEmptyBoxEmployee() {
        for(int i = 3362; i<=3370; i++) {
            Box boxById = boxesRepository.getBoxById((long) i);
            Employee emptyEmployee = employeesRepository.save(new Employee("", "", null));
            Long emptyEmployeeId = emptyEmployee.getId();
            boxById.setEmptyBoxEmployeeNo(emptyEmployeeId);
            boxesRepository.save(boxById);
        }
        return boxesRepository.getBoxById((long) 3370);
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
        Employee employee = b.getEmployee();
        String lastName = employee.getLastName();
        if((lastName != null)
                && (lastName.length() > 0)) {
            return true;
        }
        return false;
    }

}

