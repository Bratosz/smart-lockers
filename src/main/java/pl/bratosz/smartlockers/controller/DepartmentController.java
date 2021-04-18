package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.service.DepartmentService;
import pl.bratosz.smartlockers.strings.MyString;

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

    @JsonView(Views.Public.class)
    @PostMapping("/add_alias/{departmentId}/{alias}")
    public Department addAlias(@PathVariable long departmentId,
                               @PathVariable String alias) {
        alias = MyString.create(alias).get();
        return departmentService.addAlias(departmentId, alias);

    }

    @JsonView(Views.Public.class)
    @PostMapping("/create/{departmentName}/{clientId}/{mainPlantNumber}")
    public Department create(@PathVariable String departmentName,
                             @PathVariable long clientId,
                             @PathVariable int mainPlantNumber) {
        departmentName = MyString.create(departmentName).get();
        return departmentService.create(departmentName, clientId, mainPlantNumber);
    }

    public Department create(String departmentName,
                             long clientId,
                             int mainPlantNumber,
                             boolean surrogate) {
        departmentName = MyString.create(departmentName).get();
        return departmentService.create(departmentName, clientId, mainPlantNumber, surrogate);
    }

    @PostMapping("/add_plant/{departmentId}/{plantNumber}")
    public Department addPlant(@PathVariable long departmentId, @PathVariable int plantNumber) {
        return departmentService.addPlant(departmentId, plantNumber);
    }
}
