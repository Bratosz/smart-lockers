package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Location;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.service.LocationService;

import java.util.List;

@RestController
@RequestMapping("/location")
public class LocationController {
    private LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @JsonView(Views.Public.class)
    @GetMapping("/get_all/{clientId}")
    public List<Location> getAll(@PathVariable long clientId) {
        return locationService.getAll(clientId);
    }

    @PostMapping("/create/{clientId}/{locationName}")
    public Location create(@PathVariable long clientId,
                           @PathVariable String locationName) {
        locationName = locationName.toUpperCase().trim();
        return locationService.create(clientId, locationName);
    }

    @PostMapping("/assign_to_plant/{locationId}/{plantNumber}")
    public Location assignToPlant(@PathVariable long locationId,
                                  @PathVariable int plantNumber) {
        return locationService.assignToPlant(locationId, plantNumber);
    }
}
