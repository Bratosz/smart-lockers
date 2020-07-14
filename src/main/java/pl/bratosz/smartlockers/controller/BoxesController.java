package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.EmployeeCloth;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.service.BoxesService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static pl.bratosz.smartlockers.model.Locker.*;

@RestController
@RequestMapping("/boxes")
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

    @PostMapping("/create_labels/{folderName}/{sheetName}/{plantNumber}/{firstLocker}/{lastLocker}")
    public void createLabels(@PathVariable String folderName,
                             @PathVariable String sheetName,
                             @PathVariable int plantNumber,
                             @PathVariable int firstLocker,
                             @PathVariable int lastLocker) throws IOException {
    }
    @JsonView(Views.InternalForBoxes.class)
    @PostMapping("/set_empty_box_employee")
    public Box setEmptyBoxEmployee() {
        return boxesService.setEmptyBoxEmployee();
    }

    @PostMapping("/set_actual_status")
    public List<Box> setActualStatus(){
        return boxesService.setActualBoxStatus();
    }

    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/get_box/{lockerNumber}/{boxNumber}/{plantNumber}")
    public Box getBox(@PathVariable int lockerNumber,
                      @PathVariable int boxNumber,
                      @PathVariable int plantNumber) {
        return boxesService.getBox(lockerNumber, boxNumber, plantNumber);
    }
}
