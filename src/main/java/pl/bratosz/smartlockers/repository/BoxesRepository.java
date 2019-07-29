package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Locker;

@Repository
public interface BoxesRepository extends JpaRepository<Box, Long> {

}
