package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.Locker;
import pl.bratosz.smartlockers.repository.EmployeesRepository;
import pl.bratosz.smartlockers.repository.LockersRepository;

import java.util.List;

@Service
public class EmployeeService {
    private EmployeesRepository employeesRepository;
    private LockersRepository lockersRepository;

    public EmployeeService(EmployeesRepository employeesRepository, LockersRepository lockersRepository) {
        this.employeesRepository = employeesRepository;
        this.lockersRepository = lockersRepository;
    }


    public List<Employee> getAllEmployees() {
      return employeesRepository.findAll();
    }

    public Employee createEmployee(Locker.DepartmentNumber departmentNumber,
                                   Integer lockerNumber, Integer boxNumber, Employee employee) {
        Box box = lockersRepository.getBox(departmentNumber, lockerNumber, boxNumber);
        box.setBoxStatus(Box.BoxStatus.OCCUPY);
        box.setEmployee(employee);
        return employeesRepository.save(employee);
    }

    public void deleteById(Long id) {
        employeesRepository.deleteById(id);
    }
}
