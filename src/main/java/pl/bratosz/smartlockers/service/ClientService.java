package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.repository.ClientRepository;

@Service
public class ClientService {
    private ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client create(String name) {
        Client client = new Client(name);
        return clientRepository.save(client);
    }

    public Client getById(long clientId) {
        return clientRepository.getOne(clientId);
    }
}
