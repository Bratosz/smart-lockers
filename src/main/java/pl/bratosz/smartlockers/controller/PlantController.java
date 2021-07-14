package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.PlantService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/plants")
public class PlantController {

    private PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @JsonView(Views.PlantBasicInfo.class)
    @GetMapping("/get-all/{clientId}")
    public List<Plant> getAll(@PathVariable long clientId) {
        return plantService.getAll(clientId);
    }

    @GetMapping("/get_by_number/{plantNumber}")
    public Plant getByNumber(@PathVariable int plantNumber) {
        return plantService.getByNumber(plantNumber);
    }

    @PostMapping("/create/{clientId}/{plantNumber}/{name}/{login}/{password}")
    public Plant create(@PathVariable long clientId, @PathVariable int plantNumber, @PathVariable String name,
                        @PathVariable String login, @PathVariable String password) {
        return plantService.create(clientId, plantNumber, name, login, password);
    }

}
