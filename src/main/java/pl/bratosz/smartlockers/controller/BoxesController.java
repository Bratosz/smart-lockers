package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Locker;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.repository.LockersRepository;
import pl.bratosz.smartlockers.service.BoxesService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lockers/boxes")
public class BoxesController {

    private BoxesService boxesService;

    public BoxesController(BoxesService boxesService) {
        this.boxesService = boxesService;
    }

    @GetMapping
    public List<Box> getAll() {
        return boxesService.findAll();
    }

    @JsonView(Views.InternalForLockers.class)
    @PostMapping("/dismiss_employee")
    public Box dismissEmployeeByBox(@RequestBody Box box) {
        return boxesService.dismissEmployee(box);
    }

    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/{id}")
    public Box getBoxById(@PathVariable Long id) {
        return boxesService.getBoxById(id);
    }

    @PostMapping("/create_labels/{folderName}/{sheetName}/{depNumber}/{firstLocker}/{lastLocker}")
    public void createLabels(@PathVariable String folderName,
                             @PathVariable String sheetName,
                             @PathVariable Locker.DepartmentNumber depNumber,
                             @PathVariable int firstLocker,
                             @PathVariable int lastLocker) throws IOException {
        boxesService.createLabels(folderName, sheetName, depNumber, firstLocker, lastLocker);
    }


}
