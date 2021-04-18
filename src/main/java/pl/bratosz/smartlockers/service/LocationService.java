package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Location;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.repository.LocationRepository;

import java.util.List;

@Service
public class LocationService {
    private PlantService plantService;
    private ClientService clientService;
    private LocationRepository locationRepository;

    public LocationService(PlantService plantService, ClientService clientService,
                           LocationRepository locationRepository) {
        this.plantService = plantService;
        this.clientService = clientService;
        this.locationRepository = locationRepository;
    }

    public Location getByNameAndPlantNumber(String name, int plantNumber) {
        name = name.toUpperCase().trim();
        return locationRepository.getByNameAndPlantNumber(name, plantNumber);
    }

    public Location create(long clientId, String locationName) {
        return create(clientId, locationName, false);
    }

    public Location create(long clientId, String locationName, boolean surrogate) {
        Client client = clientService.getById(clientId);
        Location location = new Location(locationName, client, surrogate);
        return locationRepository.save(location);
    }

    public Location assignToPlant(long locationId, int plantNumber) {
        Plant plant = plantService.getByNumber(plantNumber);
        Location location = locationRepository.getOne(locationId);
        location.setPlant(plant);
        return locationRepository.save(location);
    }

    public List<Location> getAll(long clientId) {
        return locationRepository.getAll(clientId);
    }

    public Location getById(long id) {
        if(id == 0){
            return null;
        }
        return locationRepository.getOne(id);
    }

    public Location getSurrogateBy(Client client) {
        return locationRepository.getBySurrogateAndClient(true, client);
    }
}
