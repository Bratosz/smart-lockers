package pl.bratosz.smartlockers.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Locker;

import java.util.List;

@Repository
public interface BoxesRepository extends JpaRepository<Box, Long> {

    @Query("select b from Box b join b.locker l where " +
            "b.boxStatus = :boxStatus and " +
            "(l.department = :department or :department is null) and " +
            "(l.departmentNumber = :departmentNumber or :departmentNumber is null) and " +
            "(l.location = :location or :location is null) order by l.lockerNumber, b.boxNumber ")
    List<Box> getBoxesByParameters(@Param("department") Department department,
                                   @Param("departmentNumber") Locker.DepartmentNumber departmentNumber,
                                   @Param("location") Locker.Location location,
                                   @Param("boxStatus") Box.BoxStatus boxStatus);

    @Query("select b from Box b join b.locker l where " +
            "b.boxNumber = :boxNumber and " +
            "l.lockerNumber = :lockerNumber and " +
            "l.location = :location and " +
            "l.departmentNumber = :departmentNumber ")
    Box getBoxByParameters(Integer lockerNumber, Integer boxNumber, Locker.Location location, Locker.DepartmentNumber departmentNumber);

    Box getBoxById(Long id);
}
