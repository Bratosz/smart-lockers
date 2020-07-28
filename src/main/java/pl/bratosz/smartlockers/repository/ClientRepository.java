package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client getByPlantNumber(int plantNumber);
}
