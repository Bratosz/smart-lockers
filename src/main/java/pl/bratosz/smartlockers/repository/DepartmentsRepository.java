package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.Department;

import java.util.List;

@Repository
public interface DepartmentsRepository extends JpaRepository<Department, Long> {


    Department getByNameAndMainPlantNumber(String name, int mainPlantNumber);

    @Query("select d from Department d join d.client c where " +
            "c.id = :clientId order by d.mainPlantNumber")
    List<Department> getAll(long clientId);
}
