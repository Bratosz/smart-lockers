package pl.bratosz.smartlockers.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Locker;
import pl.bratosz.smartlockers.repository.BoxesRepository;

import java.util.List;

@Service
public class BoxesService {

    private BoxesRepository boxesRepository;

    public BoxesService(BoxesRepository boxesRepository) {
        this.boxesRepository = boxesRepository;
    }


    public Box findNextFreeBox(Department department, Locker.DepartmentNumber departmentNumber, Locker.Location location) {
        Box.BoxStatus  boxStatus = Box.BoxStatus.FREE;
        List<Box> boxes = boxesRepository.getBoxesByParameters(department, departmentNumber, location, boxStatus);
        return boxes.get(0);
    }
}
