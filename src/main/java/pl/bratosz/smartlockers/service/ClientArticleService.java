package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.repository.ClientArticlesRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientArticleService {
    private ClientArticlesRepository clientArticlesRepository;
    private ArticleService articleService;

    public ClientArticleService(ClientArticlesRepository clientArticlesRepository, ArticleService articleService) {
        this.clientArticlesRepository = clientArticlesRepository;
        this.articleService = articleService;
    }



    public List<ClientArticle> get(long clientId) {
        return clientArticlesRepository.getAllBy(clientId);
    }

    public ClientArticle get(int articleNumber, long clientId) {
        return clientArticlesRepository.getBy(articleNumber, clientId);
    }

    public ClientArticle updatePrice(float newPrice, long id) {
        ClientArticle article = clientArticlesRepository.getBy(id);
        article.setRedemptionPrice(newPrice);
        return clientArticlesRepository.save(article);
    }

    public List<ClientArticle> setDepreciationPeriod(
            int depreciationPeriod,
            long clientId) {
        List<ClientArticle> updatedArticles =
                clientArticlesRepository.getAllBy(clientId)
                        .stream()
                        .map(a -> setDepreciationPeriod(depreciationPeriod, a))
                        .collect(Collectors.toList());
        return clientArticlesRepository.saveAll(updatedArticles);
    }

    public List<ClientArticle> setDepreciationPercentageCap(
            int percentageCap,
            long clientId) {
        List<ClientArticle> updatedArticles = clientArticlesRepository.getAllBy(clientId)
                .stream()
                .map(a -> setDepreciationPercentageCap(percentageCap, a))
                .collect(Collectors.toList());
        return clientArticlesRepository.saveAll(updatedArticles);
    }

    private ClientArticle setDepreciationPercentageCap(
            int percentageCap,
            ClientArticle article) {
        article.setDepreciationPercentageCap(percentageCap);
        return article;
    }

    private ClientArticle setDepreciationPeriod(
            int depreciationPeriod,
            ClientArticle article) {
        article.setDepreciationPeriod(depreciationPeriod);
        return article;
    }


    public ClientArticle get(int articleNumber, Client client, String articleName) {
        return client.getArticles()
                .stream()
                .filter(clientArticle ->
                        clientArticle.getArticle().getNumber() == articleNumber)
                .findFirst()
                .orElseGet(() ->
                        addNewArticleToClient(articleNumber, articleName, client));
    }

    private ClientArticle addNewArticleToClient(
            int articleNumber, String articleName, Client client) {
        Article article = articleService.get(articleNumber);
        if(article == null) {
            article = articleService.addNewArticle(articleNumber, articleName);
        }
        return createDefault(article, client);
    }

    private ClientArticle createDefault(Article article, Client client) {
        ClientArticle clientArticle = new ClientArticle();
        clientArticle.setArticle(article);
        clientArticle.setAvailable(true);
        clientArticle.addClient(client);
        clientArticle.setRedemptionPrice(0);
        clientArticle.setDepreciationPeriod(42);
        clientArticle.setDepreciationPercentageCap(100);
        return clientArticlesRepository.save(clientArticle);
    }
}
