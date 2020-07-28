package pl.bratosz.smartlockers.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.service.DepartmentService;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/byName/{departmentName}")
    public Department getByName(@PathVariable String name, int plantNumber) {
        return departmentService.getByNameAndPlantNumber(name, plantNumber);
    }

}
