package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Locker;
import pl.bratosz.smartlockers.repository.LockersRepository;

@Service
public class LockersService {
    private LockersRepository lockersRepository;

    public LockersService(LockersRepository lockersRepository) {
        this.lockersRepository = lockersRepository;
    }

    public Locker deleteLockerByNumber(Long id) {
        return lockersRepository.deleteLockerById(id);
    }
}
