package pl.bratosz.smartlockers.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Location;
import pl.bratosz.smartlockers.model.Locker;

import java.util.List;

@Repository
public interface BoxesRepository extends JpaRepository<Box, Long> {

    @Override
    @Query("select b from Box b join b.locker l order by l.lockerNumber, b.boxNumber ")
    List<Box> findAll();

    @Query("select b from Box b join b.locker l where " +
            "b.boxStatus = :boxStatus and " +
            "(l.department = :department or :department is null) and " +
            "(l.plantNumber = :plantNumber or :plantNumber is null) and " +
            "(l.location = :location or :location is null) order by l.lockerNumber, b.boxNumber ")
    List<Box> getBoxesByParameters(@Param("department") Department department,
                                   @Param("plantNumber") int plantNumber,
                                   @Param("location") Location location,
                                   @Param("boxStatus") Box.BoxStatus boxStatus);


    @Query("select b from Box b join b.locker l where " +
            "b.boxNumber = :boxNumber and " +
            "l.lockerNumber = :lockerNumber and " +
            "l.location = :location and " +
            "l.plantNumber = :plantNumber ")
    Box getBox(int lockerNumber, int boxNumber, Location location, int plantNumber);

    Box getBoxById(long id);

    @Query("select b from Box b join b.locker l where " +
            "b.boxNumber = :boxNumber and " +
            "l.lockerNumber = :lockerNumber and " +
            "l.plantNumber = :plantNumber ")
    Box getBox(int lockerNumber, int boxNumber, int plantNumber);

    @Query("select b from Box b join b.locker l where " +
            "l.plantNumber = :plantNumber " +
            "and " +
            "(l.lockerNumber between :firstLocker and :lastLocker) " +
            "order by l.lockerNumber, b.boxNumber ")
    List<Box> getBoxesByLockersRange(int plantNumber, int firstLocker, int lastLocker);
}
