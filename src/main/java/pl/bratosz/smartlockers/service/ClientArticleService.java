package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.clothes.ArticleType;
import pl.bratosz.smartlockers.repository.ClientArticlesRepository;

@Service
public class ClientArticleService {
    private ClientArticlesRepository clientArticlesRepository;

    public ClientArticleService(ClientArticlesRepository clientArticlesRepository) {
        this.clientArticlesRepository = clientArticlesRepository;
    }

    public ClientArticle createDefault(ArticleType articleType, Client client) {
        ClientArticle clientArticle = new ClientArticle();
        clientArticle.setArticleType(articleType);
        clientArticle.setAvailable(true);
        clientArticle.setClient(client);
        clientArticle.setRedemptionPrice(0);
        return clientArticlesRepository.save(clientArticle);
    }
}
