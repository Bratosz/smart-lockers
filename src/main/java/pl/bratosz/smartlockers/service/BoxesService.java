package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.BoxNotAvailableException;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Locker;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.repository.EmployeesRepository;

import java.util.List;

@Service
public class BoxesService {

    private BoxesRepository boxesRepository;
    private EmployeesRepository employeesRepository;

    public BoxesService(BoxesRepository boxesRepository, EmployeesRepository employeesRepository) {
        this.boxesRepository = boxesRepository;
        this.employeesRepository = employeesRepository;
    }

    public Box findNextFreeBox(Department department, Locker.DepartmentNumber departmentNumber, Locker.Location location) throws BoxNotAvailableException {
        Box.BoxStatus  boxStatus = Box.BoxStatus.FREE;
        List<Box> boxes = boxesRepository.getBoxesByParameters(department, departmentNumber, location, boxStatus);
        if(boxes.size() == 0){
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

    public Box deleteEmployee(Box box) {
        box.setEmployee(employeesRepository.getEmployeeById(box.getEmptyBoxEmployeeNo()));
        box.setBoxStatus(Box.BoxStatus.FREE);
        return boxesRepository.save(box);
    }

    public List<Box> findAll() {
        return boxesRepository.findAll();
    }

    public Box getBoxByParameters(Integer lockerNumber, Integer boxNumber, Locker.Location location,
                                  Locker.DepartmentNumber departmentNumber) {
        return boxesRepository.getBoxByParameters(lockerNumber, boxNumber, location, departmentNumber);
    }

    public boolean isBoxFree(Integer lockerNo, Integer boxNo, Locker.Location location, Locker.DepartmentNumber departmentNumber) {
        Box box = boxesRepository.getBoxByParameters(lockerNo, boxNo, location, departmentNumber);
        if(box.getBoxStatus().equals(Box.BoxStatus.FREE)) {
            return true;
        } else {
            return  false;
        }
    }

    public Box saveBox(Box box) {
        return boxesRepository.save(box);
    }

    public Box getBoxById(Long id) {
        return boxesRepository.getBoxById(id);
    }
}
