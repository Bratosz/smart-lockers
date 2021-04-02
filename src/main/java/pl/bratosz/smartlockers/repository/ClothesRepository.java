package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.bratosz.smartlockers.model.clothes.Cloth;


import java.util.List;

@Repository
public interface ClothesRepository extends JpaRepository<Cloth, Long> {
    Cloth getClothById(long id);

    Cloth getByBarCode(long barCode);

    @Transactional
    @Modifying
    @Query("delete from Cloth c where c.id = :id ")
    void deleteById(long id);
}
