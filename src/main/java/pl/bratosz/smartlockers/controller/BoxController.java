package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.EmployeeGeneral;
import pl.bratosz.smartlockers.model.Locker;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.service.BoxService;
import java.util.List;

@RestController
@RequestMapping("/boxes")
public class BoxController {

    private BoxService boxesService;

    public BoxController(BoxService boxesService) {
        this.boxesService = boxesService;
    }

    @GetMapping
    public List<Box> getAll() {
        return boxesService.findAll();
    }

    @JsonView(Views.InternalForLockers.class)
    @PostMapping("/dismiss_employee")
    public Box dismissEmployeeByBox(@RequestBody Box b) {
        EmployeeGeneral e = b.getEmployee();
        return boxesService.dismissEmployee(b, e);
    }

    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/{id}")
    public Box getBoxById(@PathVariable Long id) {
        return boxesService.getBoxById(id);
    }


    @PostMapping("/set_actual_status")
    public List<Box> setActualStatus() {
        return boxesService.setActualBoxStatus();
    }

    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/get_box/{lockerNumber}/{boxNumber}/{plantNumber}")
    public Box getBox(@PathVariable int lockerNumber,
                      @PathVariable int boxNumber,
                      @PathVariable int plantNumber) {
        return boxesService.getBox(lockerNumber, boxNumber, plantNumber);
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/filter/{plantId}/{departmentId}/{locationId}/{boxStatus}")
    public List<Box> getFiltered(@PathVariable long plantId,
                                 @PathVariable long departmentId,
                                 @PathVariable long locationId,
                                 @PathVariable Box.BoxStatus boxStatus) {
        return boxesService.getFiltered(plantId, departmentId, locationId, boxStatus);
    }

}
