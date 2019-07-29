package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Locker;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.repository.LockersRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lockers/boxes")
public class BoxesController {

    private BoxesRepository boxesRepository;

    public BoxesController(BoxesRepository boxesRepository) {
        this.boxesRepository = boxesRepository;
    }

    @GetMapping
    public List<Box> getAll() {
        return boxesRepository.findAll();
    }
}
