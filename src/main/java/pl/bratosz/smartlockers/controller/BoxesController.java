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

}
