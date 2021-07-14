package pl.bratosz.smartlockers.service;

import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Location;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.repository.PlantsRepository;
import pl.bratosz.smartlockers.response.StandardResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlantService {

    private PlantsRepository plantsRepository;
    private ClientService clientService;
    private ScrapingService scrapingService;

    public PlantService(PlantsRepository plantsRepository, ClientService clientService, ScrapingService scrapingService) {
        this.plantsRepository = plantsRepository;
        this.clientService = clientService;
        this.scrapingService = scrapingService;
    }

    public Plant getByNumber(int plantNumber) {
        return plantsRepository.getByPlantNumber(plantNumber);
    }

    public Set<Department> getDepartments(int plantNumber) {
        Plant plant = getByNumber(plantNumber);
        return plant.getDepartments();
    }

    public boolean containsDepartment(String departmentName, int plantNumber) {
        Set<Department> departments = getDepartments(plantNumber);
        String correctDepartmentName = departmentName.toUpperCase().trim();
        boolean found = false;
        for(Department d : departments) {
            if (d.getName().toUpperCase().trim().equals(correctDepartmentName)) {
                found = true;
            }
        }
        return found;
    }

    public Set<Integer> getAllPlantNumbersOfClient(int plantNumber) {
        Client client = plantsRepository.getByPlantNumber(plantNumber).getClient();
        Set<Integer> plantNumbers = client.getPlants().stream()
                .map(plant -> plant.getPlantNumber())
                .collect(Collectors.toSet());
        return plantNumbers;
    }

    public Plant create(long clientId, int plantNumber, String name, String login, String password) {
        Client client = clientService.getById(clientId);
        Plant plant = new Plant(plantNumber, name, login, password);
        plant.setClient(client);
        return plantsRepository.save(plant);
    }

    public List<Plant> getAll(long clientId) {
        return plantsRepository.getAll(clientId);
    }

    public Plant getById(long id) {
        if(id == 0) {
            return null;
        }
        return plantsRepository.getById(id);
    }
}
