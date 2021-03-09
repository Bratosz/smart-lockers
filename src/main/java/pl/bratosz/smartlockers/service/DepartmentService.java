package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.repository.DepartmentsRepository;
import pl.bratosz.smartlockers.service.managers.DepartmentManager;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DepartmentService {

    private DepartmentsRepository departmentsRepository;
    private ClientService clientService;
    private PlantService plantService;
    private LocationService locationService;

    public DepartmentService(DepartmentsRepository departmentsRepository, ClientService clientService, PlantService plantService, LocationService locationService) {
        this.departmentsRepository = departmentsRepository;
        this.clientService = clientService;
        this.plantService = plantService;
        this.locationService = locationService;
    }

    public Department getByNameAndPlantNumber(String name, int plantNumber) {
        name = name.toUpperCase().trim();
        return departmentsRepository.getByNameAndMainPlantNumber(name, plantNumber);
    }

    public Department create(String departmentName, long clientId, int plantNumber) {
        Client client = clientService.getById(clientId);
        Set<Plant> plants = new HashSet<>();
        plants.add(plantService.getByNumber(plantNumber));
        Department department = new Department(departmentName, client, plants, plantNumber);
        return departmentsRepository.save(department);
    }

    public Department addPlant(long departmentId, int plantNumber) {
        Plant plant = plantService.getByNumber(plantNumber);
        Department department = departmentsRepository.getOne(departmentId);
        department.addPlant(plant);
        return departmentsRepository.save(department);
    }

    public List<Department> getAll(long clientId) {
        return departmentsRepository.getAll(clientId);
    }

    public Department getById(long id) {
        if(id == 0) {
            return null;
        }
        return departmentsRepository.getOne(id);
    }

    public Department getByNameAndClient(String departmentAlias, Client client) {
        Set<Department> departments = client.getDepartments();
        Optional<Department> first = departments.stream().filter(
                d -> d.getAliases().contains(departmentAlias)).findFirst();
        if(first.isPresent()){
            return first.get();
        } else {
            Optional<Department> departmentDefault = departments.stream().filter(
                    d -> d.isDepartmentDefault()).findFirst();
            if(departmentDefault.isPresent()) {
                return departmentDefault.get();
            } else {
                Department department = DepartmentManager.createDefaultDepartment(client);
                return departmentsRepository.save(department);
            }
        }
    }
}
