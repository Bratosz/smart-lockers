package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
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

    public Box findNextFreeBox(Department department, Locker.DepartmentNumber departmentNumber, Locker.Location location) {
        Box.BoxStatus  boxStatus = Box.BoxStatus.FREE;
        List<Box> boxes = boxesRepository.getBoxesByParameters(department, departmentNumber, location, boxStatus);
        return boxes.get(0);
    }

    public Box dismissEmployee(Box box) {
        box.getDismissedEmployees().add(box.getEmployee());
        box.setEmployee(employeesRepository.getEmployeeById(box.getEmptyBoxEmployeeNo()));
        box.setBoxStatus(Box.BoxStatus.FREE);
        return boxesRepository.save(box);

    }

    public List<Box> findAll() {
        return boxesRepository.findAll();
    }
}
