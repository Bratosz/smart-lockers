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
    @Override
    @Query("select l from Locker l order by l.lockerNumber ")
    List<Locker> findAll();

    @Query("select l from Locker l where " +
            "l.plantNumber = :plantNumber " +
            "order by l.lockerNumber")
    List<Locker> findAllByPlantNumber(int plantNumber);

    @Query("select l from Locker l where " +
            "(l.plantNumber = :plantNumber or :plantNumber is null) " +
            "and " +
            "(l.department = :department or :department is null) " +
            "and " +
            "(l.location = :location or :location is null) order by l.lockerNumber ")
    List<Locker> filterAllByPlantNumberAndDepartmentAndLocation(
            @Param("plant") int plantNumber,
            @Param("department") Department department,
            @Param("location") Location location);

    @Query("select l from Locker l join l.boxes b where " +
            "(l.plantNumber = :plantNumber or :plantNumber is null) " +
            "and " +
            "(l.department = :department or :department is null) " +
            "and " +
            "(l.location = :location or :location is null)" +
            "and " +
            "(b.boxStatus = :boxStatus or :boxStatus is null) " +
            "order by l.lockerNumber, b.boxNumber ")
    List<Locker> filterAllByPlantNumberAndDepartmentAndLocationAndStatus(
            @Param("plant") int plantNumber,
            @Param("department") Department department,
            @Param("location") Location location,
            @Param("boxStatus") Box.BoxStatus boxStatus);

    @Query("select count(l.plantNumber) from Locker l where l.plant.id = :plantId ")
    int getAmountOfLockersByPlantId(@Param("plantId") long plantId);

    Locker deleteLockerById(Long id);

    @Query("select l from Locker l where l.lockerNumber = :lockerNumber " +
            "and " +
            "l.plantNumber = :plantNumber " +
            "and " +
            "l.location = :location ")
    Locker getLockerByParameters(@Param("lockerNumber") Integer lockerNumber,
                                 @Param("plant") int plantNumber,
                                 @Param("location") Location location);

    @Query("select l from Locker l where l.plantNumber = :plantNumber " +
            "and " +
            "(l.lockerNumber between :firstLocker and :lastLocker) " +
            "order by l.lockerNumber ")
    List<Locker> getLockersFromRange(int plantNumber, int firstLocker, int lastLocker);
}