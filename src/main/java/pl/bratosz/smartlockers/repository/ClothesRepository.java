package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.Cloth;
import pl.bratosz.smartlockers.model.EmployeeCloth;

@Repository
public interface ClothesRepository extends JpaRepository<Cloth, Long> {
    Cloth getClothById(Long id);
}
