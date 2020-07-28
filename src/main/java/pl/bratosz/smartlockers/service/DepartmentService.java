package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.repository.DepartmentsRepository;

@Service
public class DepartmentService {

    private DepartmentsRepository departmentsRepository;

    public DepartmentService(DepartmentsRepository departmentsRepository) {
        this.departmentsRepository = departmentsRepository;
    }

    public Department getByNameAndPlantNumber(String name, int plantNumber) {
        name = name.toUpperCase().trim();
        return departmentsRepository.getByNameAndPlantNumber(name, plantNumber);
    }
}
