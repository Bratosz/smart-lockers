package pl.bratosz.smartlockers.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.RotationalCloth;

import java.util.List;

@Repository
public interface RotationalClothesRepository extends JpaRepository<RotationalCloth, Long> {

    @Override
    @Query("select r from RotationalCloth r ")
    List<RotationalCloth> findAll();

    @Query("select r from RotationalCloth r where r.isReturned = false " +
            "order by r.employee ")
    List<RotationalCloth> getReleasedRotationalClothes();
}
