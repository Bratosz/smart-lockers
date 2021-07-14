package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Plant;
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

//    @GetMapping("/load-employee/{boxId}")
//    @JsonView(Views.InternalForBoxes.class)
//    public Box loadEmployee(@PathVariable long boxId) {
//       return scrapingService.loadEmployee(boxId);
//    }

//    @PostMapping("/update-clothes")
//    public StandardResponse updateClothes(
//            @RequestBody Plant plant) {
//        return scrapingService.updateClothes(plant);
//        //refactor in to by plantId
//    }

}
