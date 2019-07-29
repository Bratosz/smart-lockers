//package pl.bratosz.smartlockers.repository;
//
//import org.springframework.stereotype.Component;
//import pl.bratosz.smartlockers.model.*;
//
//import javax.annotation.PostConstruct;
//import java.util.LinkedList;
//import java.util.List;
//
//@Component
//public class DBInit {
//    private LockersStateRepository lockersStateRepository;
//
//    public DBInit(LockersStateRepository lockersStateRepository) {
//        this.lockersStateRepository = lockersStateRepository;
//    }
//
//    @PostConstruct
//    public void init() {
//        LockersState lockersState384 = new LockersState(0, Locker.DepartmentNumber.DEP_384);
//        LockersState lockersState385 = new LockersState(0, Locker.DepartmentNumber.DEP_385);
//
//        lockersStateRepository.save(lockersState384);
//        lockersStateRepository.save(lockersState385);
//    }
//
//}
