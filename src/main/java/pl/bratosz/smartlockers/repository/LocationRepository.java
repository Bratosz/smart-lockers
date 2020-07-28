package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.bratosz.smartlockers.model.Location;
import pl.bratosz.smartlockers.model.Plant;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("select l from Location l join l.plant p where " +
            "p.plantNumber = :plantNumber and " +
            "l.name = :name ")
    Location getByNameAndPlantNumber(@Param("name")String name,
                                     @Param("plantNumber") int plantNumber);
}
