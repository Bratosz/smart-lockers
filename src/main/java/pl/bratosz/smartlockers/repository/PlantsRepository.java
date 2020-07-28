package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Plant;

import java.util.Set;

public interface PlantsRepository extends JpaRepository <Plant, Integer> {

    Plant getByPlantNumber(int plantNumber);
}
