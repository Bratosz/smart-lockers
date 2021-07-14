package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.response.ClothesLoadedResponse;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.*;

@RestController
@RequestMapping("/load")
public class PlantLoadingController {
    private PlantLoadingService plantLoadingService;

    public PlantLoadingController(PlantLoadingService plantLoadingService) {
        this.plantLoadingService = plantLoadingService;
    }

    @PostMapping("/plant-box-by-box/{plantId}")
    @JsonView(Views.Public.class)
    public StandardResponse loadLockersWithBoxesAndEmployees(
            @PathVariable long plantId) {
        return plantLoadingService.loadLockersWithBoxesAndEmployees(plantId);
    }

    @PostMapping("/clothes/{plantId}")
    public ClothesLoadedResponse loadAllClothes(@PathVariable long plantId) {
        return plantLoadingService.loadAllClothes(plantId);
    }

    @PostMapping("/clothes/{from}/{to}/{plantId}")
    public ClothesLoadedResponse loadClothesFromLockersRange(
            @PathVariable int from,
            @PathVariable int to,
            @PathVariable long plantId) {
        return plantLoadingService.loadClothesFromLockersRange(
                from, to, plantId);
    }

    @PostMapping("/test")
    public void test() {
        plantLoadingService.test();
    }


}
