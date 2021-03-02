package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Views;
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

    @GetMapping("/update-clothes/{boxId}")
    @JsonView(Views.InternalForBoxes.class)
    public Box updateEmployeeClothes(@PathVariable long boxId) throws IOException {
        try {
            return scrapingService.updateEmployeeClothes(boxId);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    @GetMapping("/load-employee/{departmentId}/{boxId}")
    @JsonView(Views.InternalForBoxes.class)
    public Box loadEmployee(
            @PathVariable long departmentId, @PathVariable long boxId) throws IOException {
        Box box = scrapingService.loadEmployee(departmentId, boxId);
        return box;
    }


}
