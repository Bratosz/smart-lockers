package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Locker;

import java.util.List;

@Repository
public interface LockersRepository extends JpaRepository<Locker, Long> {
    @Override
    @Query("select l from Locker l order by l.lockerNumber ")
    List<Locker> findAll();

    @Query("select l from Locker l where " +
            "(l.departmentNumber = :departmentNumber or :departmentNumber is null) " +
            "and " +
            "(l.department = :department or :department is null) " +
            "and " +
            "(l.location = :location or :location is null) order by l.lockerNumber ")
    List<Locker> filterAllByDepartmentNoAndDepartmentAndLocation(
            @Param("departmentNumber") Locker.DepartmentNumber departmentNumber,
            @Param("department") Department department,
            @Param("location") Locker.Location location);

    @Query("select count(l.departmentNumber) from Locker l where l.departmentNumber = :departmentNumber ")
    int getAmountOfLockersByDepartmentNumber(@Param("departmentNumber") Locker.DepartmentNumber departmentNumber);

    @Query("select b from Locker l join l.boxes b " +
            "where l.lockerNumber = :lockerNumber " +
            "and " +
            "l.departmentNumber = :departmentNumber " +
            "and " +
            "b.boxNumber = :boxNumber ")
    Box getBox(@Param("departmentNumber") Locker.DepartmentNumber departmentNumber,
               @Param("lockerNumber") Integer lockerNumber,
               @Param("boxNumber") Integer boxNumber);


}