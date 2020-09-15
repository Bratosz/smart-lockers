package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.Cloth;


import java.util.List;

@Repository
public interface ClothesRepository extends JpaRepository<Cloth, Long> {



    @Query("select c from Cloth c where c.rotational = :rotational")
    List<Cloth> findAllClothesByRotationalValue(@Param("rotational") boolean rotational);

    @Query("select c from Cloth c where c.rotational = true and c.releaseAsRotational = true ")
    List<Cloth> getReleasedRotationalClothes();

    Cloth getClothById(long id);
}
