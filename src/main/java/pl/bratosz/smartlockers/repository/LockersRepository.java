package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Location;
import pl.bratosz.smartlockers.model.Locker;

import java.util.List;

@Repository
public interface LockersRepository extends JpaRepository<Locker, Long> {

    @Query("select l from Locker l where l.plant.client.id = :clientId order by l.plant.plantNumber, l.lockerNumber ")
    List<Locker> getAllByClientId(@Param("clientId") long clientId);

    @Query("select l from Locker l where " +
            "l.plant.plantNumber = :plantNumber " +
            "order by l.lockerNumber")
    List<Locker> findAllByPlantNumber(int plantNumber);


    @Query("select count(l.plant.id) from Locker l where l.plant.id = :plantId ")
    int getAmountOfLockersByPlantId(@Param("plantId") long plantId);

    Locker deleteLockerById(Long id);

    @Query("select l from Locker l where l.lockerNumber = :lockerNumber " +
            "and " +
            "l.plant.plantNumber = :plantNumber " +
            "and " +
            "l.location = :location ")
    Locker getLockerByParameters(@Param("lockerNumber") Integer lockerNumber,
                                 @Param("plant") int plantNumber,
                                 @Param("location") Location location);

    @Query("select l from Locker l where l.plant.plantNumber = :plantNumber " +
            "and " +
            "(l.lockerNumber between :firstLocker and :lastLocker) " +
            "order by l.lockerNumber ")
    List<Locker> getLockersFromRange(int plantNumber, int firstLocker, int lastLocker);

    @Query("select l from Locker l where " +
            "(l.plant.id = :plantId or :plantId is null) " +
            "and " +
            "(l.department.id = :departmentId or :departmentId is null) " +
            "and " +
            "(l.location.id = :locationId or :locationId is null) order by l.lockerNumber ")
    List<Locker> filterAllByPlantAndDepartmentAndLocation(
            @Param("plantId") long plantId,
            @Param("departmentId") long departmentId,
            @Param("locationId") long locationId);


    @Query("select l from Locker l where " +
            "l.plant.id = :plantId " +
            "and " +
            "l.lockerNumber = :lockerNumber")
    List<Locker> getLockersByPlantAndNumber(
            @Param("plantId") long plantId,
            @Param("lockerNumber") int lockerNumber);
}