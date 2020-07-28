package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Location;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.repository.LocationRepository;

import java.util.Optional;

@Service
public class LocationService {
    private LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location getByNameAndPlantNumber(String name, int plantNumber) {
        name = name.toUpperCase().trim();
        return locationRepository.getByNameAndPlantNumber(name, plantNumber);
    }
}
