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

    public Locker changeLocation(Integer lockerNumber, Locker.DepartmentNumber departmentNumber,
                                 Locker.Location location, Locker.Location desiredLocation) {
        Locker locker = getLockerByParameters(lockerNumber, departmentNumber, location);
        locker.setLocation(desiredLocation);
        return lockersRepository.save(locker);
    }

    public Locker getLockerByParameters(Integer lockerNumber, Locker.DepartmentNumber departmentNumber,
                                        Locker.Location location) {
        return lockersRepository.getLockerByParameters(lockerNumber, departmentNumber, location);
    }

    public List<Locker> getLockersFromRange(Locker.DepartmentNumber depNumber, int firstLocker, int lastLocker) {
        return lockersRepository.getLockersFromRange(depNumber, firstLocker, lastLocker);
    }
}
