package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.clothes.Cloth;


import java.util.List;

@Repository
public interface ClothesRepository extends JpaRepository<Cloth, Long> {
    Cloth getClothById(long id);
}
