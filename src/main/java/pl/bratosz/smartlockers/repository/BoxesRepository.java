package pl.bratosz.smartlockers.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.bratosz.smartlockers.model.*;

import java.util.List;

@Repository
public interface BoxesRepository extends JpaRepository<Box, Long> {

    @Override
    @Query("select b from Box b join b.locker l order by l.lockerNumber, b.boxNumber ")
    List<Box> findAll();

    @Query("select b from Box b join b.locker l where " +
            "b.boxStatus = :boxStatus and " +
            "(l.department = :department or :department is null) and " +
            "(l.plant.plantNumber = :plantNumber or :plantNumber is null) and " +
            "(l.location = :location or :location is null) order by l.lockerNumber, b.boxNumber ")
    List<Box> getBoxesByParameters(@Param("department") Department department,
                                   @Param("plant") int plantNumber,
                                   @Param("location") Location location,
                                   @Param("boxStatus") Box.BoxStatus boxStatus);


    @Query("select b from Box b join b.locker l where " +
            "b.boxNumber = :boxNumber and " +
            "l.lockerNumber = :lockerNumber and " +
            "l.location = :location and " +
            "l.plant.plantNumber = :plantNumber ")
    Box getBox(int lockerNumber, int boxNumber, Location location, int plantNumber);

    Box getById(long id);

    @Query("select b from Box b " +
            "where b.locker.plant.id = :plantId " +
            "and b.locker.lockerNumber = :lockerNumber " +
            "and b.boxNumber = :boxNumber")
    Box getBox(long plantId, int lockerNumber, int boxNumber);

    @Query("select b from Box b join b.locker l where " +
            "l.plant.plantNumber = :plantNumber " +
            "and " +
            "(l.lockerNumber between :firstLocker and :lastLocker) " +
            "order by l.lockerNumber, b.boxNumber ")
    List<Box> getBoxesByLockersRange(int plantNumber, int firstLocker, int lastLocker);

    @Query("select b from Box b " +
            "where b.locker.plant.id = :plantId " +
            "and (b.locker.department.id = :departmentId or :departmentId is null) " +
            "and (b.locker.location.id = :locationId or :locationId is null) " +
            "and (b.boxStatus = :boxStatus or :boxStatus is null) ")
    List<Box> getFiltered(
            Long plantId,
            Long departmentId,
            Long locationId,
            Box.BoxStatus boxStatus);
    @Transactional
    @Modifying
    @Query("delete from Box b where b.id = :id ")
    void deleteHardById(long id);

    @Query("select b from Box b " +
            "where b.employee.lastName like %:lastName% " +
            "and b.locker.plant.client.id = :clientId " +
            "order by b.locker.plant.plantNumber, " +
            "b.locker.lockerNumber, " +
            "b.boxNumber")
    List<Box> getByLastNameAndClientId(String lastName, long clientId);

    @Query("select b from Box b " +
            "where b.locker.lockerNumber = :lockerNumber " +
            "and b.locker.plant.id = :plantId")
    List<Box> getByLockerNumberAndPlant(int lockerNumber, long plantId);

    @Query("select b from Box b " +
            "where b.locker.plant.id = :plantId " +
            "and b.locker.department.id = :departmentId " +
            "and b.locker.location.id = :locationId " +
            "and b.boxStatus = :boxStatus " +
            "order by b.locker.lockerNumber, " +
            "b.boxNumber")
    List<Box> getBoxesByParameters(
            long plantId,
            long departmentId,
            long locationId,
            Box.BoxStatus boxStatus);
}
