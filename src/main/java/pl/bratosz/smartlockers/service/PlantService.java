package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.repository.PlantsRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlantService {

    private PlantsRepository plantsRepository;

    public PlantService(PlantsRepository plantsRepository) {
        this.plantsRepository = plantsRepository;
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
}
