package pl.bratosz.smartlockers.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Locker;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.service.ClientService;

@RestController
@RequestMapping("/client")
public class ClientController {
    private ClientService clientService;
    private PlantController plantController;
    private DepartmentController departmentController;
    private LocationController locationController;
    private LockerController lockerController;

    public ClientController(ClientService clientService, PlantController plantController, DepartmentController departmentController, LocationController locationController, LockerController lockerController) {
        this.clientService = clientService;
        this.plantController = plantController;
        this.departmentController = departmentController;
        this.locationController = locationController;
        this.lockerController = lockerController;
    }

    @PostMapping("/create/{name}")
    public Client create(@PathVariable String name) {
        return clientService.create(name);
    }

    @PostMapping("/generate")
    public Client generate() {
        Client client = create("LEAR");
        long clientId = client.getId();
        Plant plant1 = plantController.create(clientId, 384,
                "384 LEAR Zakład Główny", "LEAR", "384LEAR#1");
        Plant plant2 = plantController.create(clientId, 385,
                "385 LEAR Zakład Główny", "LEAR", "385LEAR#1");
        Department department1 = departmentController.create(
                "STRUCTURES", clientId, 384);
        Department department2 = departmentController.create(
                "JIT", clientId, 385);
        long id1 = locationController.create(clientId, "Stara hala").getId();
        long id2 = locationController.create(
                clientId, "Nowa hala - parter").getId();
        long id3 = locationController.create(
                clientId, "Nowa hala - piętro (antresola)").getId();
        long id4 = locationController.create(
                clientId, "Nowa hala - piętro (korytarz)").getId();
        long id5 = locationController.create(
                clientId, "Nowa hala - JIT parter").getId();

        locationController.assignToPlant(id1, 384);
        locationController.assignToPlant(id2, 384);
        locationController.assignToPlant(id3, 384);

        locationController.assignToPlant(id3, 385);
        locationController.assignToPlant(id4, 385);
        locationController.assignToPlant(id5, 385);

        Locker locker = new Locker(1,10);
        lockerController.create(plant1.getId(), department1.getId(), id1, locker);
        return client;
    }
}
