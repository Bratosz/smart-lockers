package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.repository.ClothesStatusRepository;

@Service
public class ClothStatusService {
    private ClothesStatusRepository clothesStatusRepository;

    public ClothStatusService(ClothesStatusRepository clothesStatusRepository) {
        this.clothesStatusRepository = clothesStatusRepository;
    }
}
