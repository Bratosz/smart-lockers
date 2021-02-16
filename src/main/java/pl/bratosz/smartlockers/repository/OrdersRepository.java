package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.orders.ClothOrder;

import java.util.Set;

@Repository
public interface OrdersRepository extends JpaRepository<ClothOrder, Long> {

    @Query("select o from ClothOrder o where o.clothToRelease.employee.id = :employeeId")
    Set<ClothOrder> getByEmployeeId(long employeeId);

    @Query("select  c from ClothOrder c where c.id = :id ")
    ClothOrder getById(long id);
}
