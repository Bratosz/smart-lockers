package pl.bratosz.smartlockers.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.BoxNotAvailableException;
import pl.bratosz.smartlockers.exels.ExcelSave;
import pl.bratosz.smartlockers.exels.ExcelWriter;
import pl.bratosz.smartlockers.formaters.StringFormater;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.repository.EmployeesRepository;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class BoxesService {

    private BoxesRepository boxesRepository;
    private EmployeesRepository employeesRepository;
    private LockersService lockersService;


    public BoxesService(BoxesRepository boxesRepository, EmployeesRepository employeesRepository) {
        this.boxesRepository = boxesRepository;
        this.employeesRepository = employeesRepository;
    }

    public Box findNextFreeBox(Department department, Locker.DepartmentNumber departmentNumber, Locker.Location location) throws BoxNotAvailableException {
        Box.BoxStatus boxStatus = Box.BoxStatus.FREE;
        List<Box> boxes = boxesRepository.getBoxesByParameters(department, departmentNumber, location, boxStatus);
        if (boxes.size() == 0) {
            throw new BoxNotAvailableException();
        }
        return boxes.stream().findFirst().get();
    }

    public Box dismissEmployee(Box box) {
        box.getDismissedEmployees().add(box.getEmployee());
        box.setEmployee(employeesRepository.getEmployeeById(box.getEmptyBoxEmployeeNo()));
        box.setBoxStatus(Box.BoxStatus.FREE);
        return boxesRepository.save(box);
    }

    public Employee releaseEmployeeFromBox(Box box) {
        Employee employee = box.getEmployee();
        employee.getBoxesOccupiedInPast().add(box);
        box.setEmployee(employeesRepository.getEmployeeById(box.getEmptyBoxEmployeeNo()));
        box.setBoxStatus(Box.BoxStatus.FREE);
        boxesRepository.save(box);
        return employee;
    }

    public Box setEmployee(Employee employee, Box box) {
        box.setEmployee(employee);
        box.setBoxStatus(Box.BoxStatus.OCCUPY);
        return boxesRepository.save(box);
    }

    public List<Box> findAll() {
        return boxesRepository.findAll();
    }

    public Box getBoxByParameters(Integer lockerNumber, Integer boxNumber, Locker.Location location,
                                  Locker.DepartmentNumber departmentNumber) {
        return boxesRepository.getBoxByParameters(lockerNumber, boxNumber, location, departmentNumber);
    }

    public Box getBoxByParameters(int lockerNumber, int boxNumber, Locker.DepartmentNumber departmentNumber) {
        return boxesRepository.getBoxByParameters(lockerNumber, boxNumber, departmentNumber);
    }

    public boolean isBoxFree(Integer lockerNo, Integer boxNo, Locker.Location location, Locker.DepartmentNumber departmentNumber) {
        Box box = boxesRepository.getBoxByParameters(lockerNo, boxNo, location, departmentNumber);
        if (box.getBoxStatus().equals(Box.BoxStatus.FREE)) {
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

            Box box = new Box(i, Box.BoxStatus.FREE, employee.getId());
            box.setEmployee(employee);
            box.setDismissedEmployees(employees);
            boxes.add(box);
        }
        return boxes;
    }

    public List<Box> getBoxesByLockersRange(Locker.DepartmentNumber depNumber, int firstLocker, int lastLocker) {
        return boxesRepository.getBoxesByLockersRange(depNumber, firstLocker, lastLocker);
    }
}

