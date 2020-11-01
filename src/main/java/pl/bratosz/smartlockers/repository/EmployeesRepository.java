package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.Employee;


import java.util.List;

@Repository
public interface EmployeesRepository extends JpaRepository<Employee, Long> {
    List<Employee> getByFirstNameAndLastName(String firstName, String lastName);


    Employee getEmployeeById(Long id);

    @Query("select e from Employee e " +
            "where e.lastName like %:lastName% ")
    List<Employee> getEmployeesByLastName(@Param("lastName") String lastName);

    Integer deleteEmployeeById(Long id);

    @Query("select e from Employee e " +
    "where e.firstName like %:firstName%")
    List<Employee> getEmployeesByFirstName(@Param("firstName") String firstName);

}
