package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.EmployeeGeneral;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.BoxService;
import pl.bratosz.smartlockers.strings.MyString;

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

    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/{id}")
    public Box getBoxById(@PathVariable Long id) {
        return boxesService.getBoxById(id);
    }

    @JsonView(Views.EmployeeCompleteInfo.class)
    @GetMapping("/get-with-complete-employee-info/{id}")
    public Box getBoxWithCompleteEmployeeInfo(@PathVariable long id) {
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

    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/get-filtered/{plantId}/{departmentId}/{locationId}/{boxStatus}")
    public List<Box> getFiltered(@PathVariable long plantId,
                                 @PathVariable long departmentId,
                                 @PathVariable long locationId,
                                 @PathVariable Box.BoxStatus boxStatus) {
        return boxesService.getFiltered(
                plantId, departmentId, locationId, boxStatus);
    }

    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/get-by-last-name/{lastName}/{clientId}")
    public List<Box> getByLastName(
            @PathVariable String lastName,
            @PathVariable long clientId) {
        lastName = MyString.create(lastName).get();
        return boxesService.getByLastName(lastName, clientId);
    }

    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/get-by-locker-number-and-plant" +
            "/{lockerNumber}/{plantId}")
    public List<Box> getByLockerNumberAndPlant(
            @PathVariable int lockerNumber,
            @PathVariable long plantId) {
        return boxesService.getByLockerNumberAndPlant(
                lockerNumber, plantId);
    }

    @JsonView(Views.Public.class)
    @DeleteMapping("/delete/{boxId}")
    public StandardResponse deleteBy(@PathVariable long boxId) {
        return boxesService.hardDeleteBy(boxId);
    }

}
