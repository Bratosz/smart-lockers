package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.repository.ClientArticlesRepository;

import java.util.List;

@Service
public class ClientArticleService {
    private ClientArticlesRepository clientArticlesRepository;

    public ClientArticleService(ClientArticlesRepository clientArticlesRepository) {
        this.clientArticlesRepository = clientArticlesRepository;
    }

    public ClientArticle createDefault(Article article, Client client) {
        ClientArticle clientArticle = new ClientArticle();
        clientArticle.setArticle(article);
        clientArticle.setAvailable(true);
        clientArticle.setClient(client);
        clientArticle.setRedemptionPrice(0);
        return clientArticlesRepository.save(clientArticle);
    }

    public List<ClientArticle> get(long clientId) {
        return clientArticlesRepository.getBy(clientId);
    }
}
