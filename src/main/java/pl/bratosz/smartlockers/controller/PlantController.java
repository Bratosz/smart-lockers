package pl.bratosz.smartlockers.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.service.PlantService;

@RestController
@RequestMapping("/plants")
public class PlantController {

    private PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @GetMapping("/get_by_number/{plantNumber}")
    public Plant getByNumber(@PathVariable int plantNumber) {
        return plantService.getByNumber(plantNumber);
    }
}
