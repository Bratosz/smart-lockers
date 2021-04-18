package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.ArticleNotExistException;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.ClothType;
import pl.bratosz.smartlockers.repository.ArticlesRepository;
import pl.bratosz.smartlockers.resolvers.ClothTypeResolver;

@Service
public class ArticleService {
    private ArticlesRepository articlesRepository;
    private ClientService clientService;

    public ArticleService(ArticlesRepository articlesRepository, ClientService clientService) {
        this.articlesRepository = articlesRepository;
        this.clientService = clientService;
    }

    public Article get(int articleNumber) {
        return articlesRepository.getByArticleNumber(articleNumber);
    }

    public Article get(int articleNumber, String articleName,  Client client) {
        return client.getArticlesWithPrices()
                .keySet()
                .stream()
                .filter(article -> article.getArticleNumber() == articleNumber)
                .findFirst()
                .orElseGet(() ->
                        addNewArticle(articleNumber, articleName, client));
    }

    public Article addNewArticle(int articleNumber, String articleName, Client client) {
        Article article = articlesRepository.getByArticleNumber(articleNumber);
        if(article == null) {
            ClothTypeResolver clothTypeResolver = new ClothTypeResolver();
            ClothType clothType = clothTypeResolver.resolve(articleName);
            article = articlesRepository.save(
                    new Article(articleNumber, articleName, clothType));
        }
        clientService.add(article, client);
        return article;
    }

    public Article determineDesiredArticle(int articleNumber, Article article) throws ArticleNotExistException {
        if(articleNumber == 0) {
            return article;
        } else{
             Article a = get(articleNumber);
             if(a.equals(null)) {
                 throw new ArticleNotExistException("Article number: " + articleNumber);
             } else {
                 return a;
             }
        }
    }
}
