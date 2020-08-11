package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.service.DepartmentService;

import java.util.List;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    private DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @JsonView(Views.Public.class)
    @GetMapping("/get_all/{clientId}")
    public List<Department> getAll(@PathVariable long clientId) {
        return departmentService.getAll(clientId);
    }

    @GetMapping("/byName/{departmentName}/{mainPlantNumber}")
    public Department getByName(@PathVariable String departmentName, @PathVariable int mainPlantNumber) {
        return departmentService.getByNameAndPlantNumber(departmentName, mainPlantNumber);
    }

    @PostMapping("/create/{departmentName}/{clientId}/{mainPlantNumber}")
    public Department create(@PathVariable String departmentName,
                             @PathVariable long clientId,
                             @PathVariable int mainPlantNumber) {
        departmentName = departmentName.trim().toUpperCase();
        return departmentService.create(departmentName, clientId, mainPlantNumber);
    }

    @PostMapping("/add_plant/{departmentId}/{plantNumber}")
    public Department addPlant(@PathVariable long departmentId, @PathVariable int plantNumber) {
        return departmentService.addPlant(departmentId, plantNumber);
    }
}
