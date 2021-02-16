package pl.bratosz.smartlockers.controller;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.response.DataLoadedResponse;
import pl.bratosz.smartlockers.service.BoxService;
import pl.bratosz.smartlockers.service.ClientService;
import pl.bratosz.smartlockers.service.UserService;
import pl.bratosz.smartlockers.service.exels.LoadType;

import java.io.IOException;

@RestController
@RequestMapping("/client")
public class ClientController {
    private ClientService clientService;
    private PlantController plantController;
    private DepartmentController departmentController;
    private LocationController locationController;
    private LockerController lockerController;
    private EmployeeController employeeController;
    private UserService userService;
    private BoxService boxService;

    public ClientController(ClientService clientService, PlantController plantController, DepartmentController departmentController, LocationController locationController, LockerController lockerController, EmployeeController employeeController, UserService userService, BoxService boxService) {
        this.clientService = clientService;
        this.plantController = plantController;
        this.departmentController = departmentController;
        this.locationController = locationController;
        this.lockerController = lockerController;
        this.employeeController = employeeController;
        this.userService = userService;
        this.boxService = boxService;
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
        Plant plant3 = plantController.create(clientId, 386,
                "386 LEAR Mantrans", "LEAR", "386LEAR#1");
        Department department1 = departmentController.create(
                "STRUCTURES", clientId, 384);
        Department department2 = departmentController.create(
                "JIT", clientId, 385);
        Department department3 = departmentController.create(
                "MANTRANS", clientId, 386);
        long id1 = locationController.create(
                clientId, "Stara szatnia - piwnica").getId();
        long id2 = locationController.create(
                clientId, "Stara szatnia - parter").getId();
        long id3 = locationController.create(
                clientId, "Nowa szatnia - parter").getId();
        long id4 = locationController.create(
                clientId, "Nowa szatnia - piętro (antresola)").getId();
        long id5 = locationController.create(
                clientId, "Nowa szatnia - piętro (korytarz)").getId();
        long id6 = locationController.create(
                clientId, "Nowa hala - produkcja").getId();
        long id7 = locationController.create(
                clientId, "SEGRO").getId();

        locationController.assignToPlant(id1, 384);
        locationController.assignToPlant(id2, 384);
        locationController.assignToPlant(id3, 384);
        locationController.assignToPlant(id4, 384);

        locationController.assignToPlant(id5, 385);
        locationController.assignToPlant(id6, 385);

        locationController.assignToPlant(id7, 386);

        userService.create("Admin", "Admin");

        return client;
    }

    @PostMapping("/load_basic_db/{clientId}/{loadType}")
    public DataLoadedResponse loadBasicDataBase(
            @PathVariable long clientId,
            @PathVariable LoadType loadType,
            @RequestParam("file")MultipartFile basicDataBaseToLoad) {
        try {
            XSSFWorkbook wb = new XSSFWorkbook(basicDataBaseToLoad.getInputStream());
            DataLoadedResponse response = clientService.loadDataBase(clientId, loadType, wb);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return DataLoadedResponse.createFailLoadDataFromFile(e.getMessage());
        }
    }
}
