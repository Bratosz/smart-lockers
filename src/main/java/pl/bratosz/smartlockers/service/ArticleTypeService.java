package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.ArticleNotExistException;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.clothes.ArticleType;
import pl.bratosz.smartlockers.model.clothes.ClothType;
import pl.bratosz.smartlockers.repository.ArticleTypesRepository;
import pl.bratosz.smartlockers.resolvers.ClothTypeResolver;

@Service
public class ArticleTypeService {
    private ArticleTypesRepository articleTypesRepository;
    private ClientService clientService;

    public ArticleTypeService(ArticleTypesRepository articleTypesRepository, ClientService clientService) {
        this.articleTypesRepository = articleTypesRepository;
        this.clientService = clientService;
    }

    public ArticleType get(int articleNumber) {
        return articleTypesRepository.getByArticleTypeNumber(articleNumber);
    }

    public ArticleType get(int articleNumber, String articleName, Client client) {
        return client.getArticles()
                .stream()
                .map(article -> article.getArticleType())
                .filter(articleType -> articleType.getArticleNumber() == articleNumber)
                .findFirst()
                .orElseGet(() ->
                        addNewArticleType(articleNumber, articleName, client));
    }

    public ArticleType addNewArticleType(int articleNumber, String articleName, Client client) {
        ArticleType articleType = articleTypesRepository.getByArticleTypeNumber(articleNumber);
        if(articleType == null) {
            ClothTypeResolver clothTypeResolver = new ClothTypeResolver();
            ClothType clothType = clothTypeResolver.resolve(articleName);
            articleType = articleTypesRepository.save(
                    new ArticleType(articleNumber, articleName, clothType));
        }
        clientService.addArticle(articleType, client);
        return articleType;
    }

    public ArticleType determineDesiredArticle(int articleNumber, ArticleType articleType) throws ArticleNotExistException {
        if(articleNumber == 0) {
            return articleType;
        } else{
             ArticleType a = get(articleNumber);
             if(a.equals(null)) {
                 throw new ArticleNotExistException("ArticleType number: " + articleNumber);
             } else {
                 return a;
             }
        }
    }
}
