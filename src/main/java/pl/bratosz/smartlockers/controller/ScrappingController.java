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

    @GetMapping("/load-employee/{boxId}")
    @JsonView(Views.InternalForBoxes.class)
    public Box loadEmployee(@PathVariable long boxId) {
       return scrapingService.loadEmployee(boxId);
    }

    @PostMapping("/load-plant-box-by-box/{plantId}")
    @JsonView(Views.Public.class)
    public LockersLoadReport loadPlantBoxByBox(@PathVariable long plantId) {
        return scrapingService.loadPlantBoxByBox(plantId);
    }

    @PostMapping("/load-plant-locker-by-locker/" +
            "{plantId}/{firstLockerNumber}/{lastLockerNumber}/{capacity}/{withClothes}")
    @JsonView(Views.Public.class)
    public StandardResponse loadLockersWithClothes(
            @PathVariable long plantId,
            @PathVariable int firstLockerNumber,
            @PathVariable int lastLockerNumber,
            @PathVariable int capacity,
            @PathVariable boolean withClothes) {
        return scrapingService.loadLockers(
                plantId, firstLockerNumber, lastLockerNumber, capacity, withClothes);
    }

    @PostMapping("/load-locker/{lockerId}")
    @JsonView(Views.InternalForLockers.class)
    public LockersLoadReport loadLocker(@PathVariable long lockerId) {
        return scrapingService.loadLocker(lockerId);
    }


}
