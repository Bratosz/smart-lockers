package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.repository.ClientRepository;

import java.util.Set;

@Service
public class ClientService {
    ClientRepository clientRepository;


    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;

    }

    public Client getByPlantNumber(int plantNumber) {
    return clientRepository.getByPlantNumber(plantNumber);
    }
}
