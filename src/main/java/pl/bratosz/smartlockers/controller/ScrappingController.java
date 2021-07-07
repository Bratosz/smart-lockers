package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.LockersLoadReport;
import pl.bratosz.smartlockers.service.ScrapingService;

import java.io.IOException;

@RestController
@RequestMapping("/scrap")
public class ScrappingController {
    private ScrapingService scrapingService;

    @Autowired
    public ScrappingController(ScrapingService scrapingService) {
        this.scrapingService = scrapingService;
    }

    @GetMapping("/update-clothes/{boxId}/{userId}")
    @JsonView(Views.InternalForBoxes.class)
    public Box updateEmployeeClothes(@PathVariable long boxId,
                                     @PathVariable long userId) throws IOException {
        try {
            return scrapingService.updateEmployeeClothes(boxId, userId);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    @PostMapping("/test/{plantId}")
    public void test(@PathVariable long plantId) {
        scrapingService.test(plantId);
    }

    @GetMapping("/load-employee/{boxId}")
    @JsonView(Views.InternalForBoxes.class)
    public Box loadEmployee(@PathVariable long boxId) {
       return scrapingService.loadEmployee(boxId);
    }


    @PostMapping("/load-plant-box-by-box/{plantId}")
    @JsonView(Views.Public.class)
    public StandardResponse loadLockersWithBoxesAndEmployees(@PathVariable long plantId) {
        return scrapingService.loadLockersWithBoxesAndEmployees(plantId);
    }

    @PostMapping("/update-clothes/{plantId}")
    public StandardResponse updateClothes(@PathVariable long plantId) {
        return scrapingService.updateClothes(plantId);
    }

    @PostMapping("/load-clothes/{plantId}")
    public StandardResponse loadClothes(@PathVariable long plantId) {
        return scrapingService.loadClothes(plantId);
    }


}
