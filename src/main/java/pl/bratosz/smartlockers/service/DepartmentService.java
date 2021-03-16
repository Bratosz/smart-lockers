package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.DepartmentAlias;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.repository.DepartmentsRepository;
import pl.bratosz.smartlockers.service.managers.DepartmentManager;

import java.util.*;

@Service
public class DepartmentService {

    private DepartmentsRepository departmentsRepository;
    private ClientService clientService;
    private PlantService plantService;
    private LocationService locationService;
    private DepartmentAliasService departmentAliasService;

    public DepartmentService(DepartmentsRepository departmentsRepository, ClientService clientService, PlantService plantService, LocationService locationService, DepartmentAliasService departmentAliasService) {
        this.departmentsRepository = departmentsRepository;
        this.clientService = clientService;
        this.plantService = plantService;
        this.locationService = locationService;
        this.departmentAliasService = departmentAliasService;
    }

    public Department getByNameAndPlantNumber(String name, int plantNumber) {
        name = name.toUpperCase().trim();
        return departmentsRepository.getByNameAndMainPlantNumber(name, plantNumber);
    }

    public Department create(String departmentName, long clientId, int plantNumber) {
        Client client = clientService.getById(clientId);

        Set<Plant> plants = new HashSet<>();
        plants.add(
                plantService.getByNumber(plantNumber));

        List<DepartmentAlias> aliases = new LinkedList<>();
        aliases.add(
                departmentAliasService.create(departmentName));

        Department department = new Department(
                departmentName, client, plants, plantNumber, aliases);
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
        if (id == 0) {
            return null;
        }
        return departmentsRepository.getOne(id);
    }

    public Department getByNameAndClient(String departmentAlias, Client client) {
        Set<Department> departments = client.getDepartments();
        for (Department d : departments) {
            if (d.getAliases().stream().anyMatch(
                    alias -> alias.equals(departmentAlias))) {
                return d;
            }
        }
        Optional<Department> departmentDefault = departments.stream().filter(
                d -> d.isDepartmentDefault()).findFirst();
        if (departmentDefault.isPresent()) {
            return departmentDefault.get();
        } else {
            Department department = DepartmentManager.createDefaultDepartment(client);
            return departmentsRepository.save(department);
        }
    }

    public Department addAlias(long departmentId, String alias) {
        Department dep = departmentsRepository.getDepartmetById(departmentId);
        DepartmentAlias departmentAlias = departmentAliasService.create(alias);
        dep.addAlias(departmentAlias);
        return departmentsRepository.save(dep);
    }
}
