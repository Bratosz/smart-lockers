package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.Locker;
import pl.bratosz.smartlockers.repository.LockersRepository;

import java.util.LinkedList;
import java.util.List;

@Service
public class LockersService {
    private LockersRepository lockersRepository;
    private EmployeeService employeeService;

    public LockersService(LockersRepository lockersRepository, EmployeeService employeeService) {
        this.lockersRepository = lockersRepository;
        this.employeeService = employeeService;
    }

    public Locker deleteLockerByNumber(Long id) {
        return lockersRepository.deleteLockerById(id);
    }
}
