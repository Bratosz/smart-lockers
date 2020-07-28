package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.Department;

@Repository
public interface DepartmentsRepository extends JpaRepository<Department, Long> {


    Department getByNameAndPlantNumber(String name, int plantNumber);
}
