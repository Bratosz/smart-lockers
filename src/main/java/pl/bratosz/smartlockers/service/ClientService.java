package pl.bratosz.smartlockers.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.Locker;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.repository.ClientRepository;
import pl.bratosz.smartlockers.response.DataLoadedResponse;
import pl.bratosz.smartlockers.service.exels.DataBaseLoader;
import pl.bratosz.smartlockers.service.exels.LoadType;

import java.util.List;

@Service
public class ClientService {
    private ClientRepository clientRepository;
    private ClientArticleService clientArticleService;

    @Autowired
    private LockerService lockerService;

    public ClientService(ClientRepository clientRepository, ClientArticleService clientArticleService) {
        this.clientRepository = clientRepository;
        this.clientArticleService = clientArticleService;
    }

    public Client create(String name) {
        Client client = new Client(name);
        return clientRepository.save(client);
    }

    public DataLoadedResponse loadDataBase(long clientId, LoadType loadType, XSSFWorkbook wb) {
        Client client = getById(clientId);
        DataBaseLoader dbLoader = new DataBaseLoader(wb, loadType, client);
        List<Locker> lockers = dbLoader.loadDataBase();
        lockerService.saveLockers(lockers);
        DataLoadedResponse response = DataLoadedResponse.createLockersBoxesAndEmployeesLoadedSuccesfully(lockers);
        return response;
    }

    public Client getById(long clientId) {
        return clientRepository.getById(clientId);
    }
}
