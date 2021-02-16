package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.ArticleNotExistException;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.ClothType;
import pl.bratosz.smartlockers.repository.ArticlesRepository;
import pl.bratosz.smartlockers.resolvers.ClothTypeResolver;

@Service
public class ArticleService {
    private ArticlesRepository articlesRepository;

    public ArticleService(ArticlesRepository articlesRepository) {
        this.articlesRepository = articlesRepository;
    }

    public Article get(int articleNumber) {
        return articlesRepository.getByArticleNumber(articleNumber);
    }

    public Article addNewArticle(int articleNumber, String articleName) {
        ClothTypeResolver clothTypeResolver = new ClothTypeResolver();
        ClothType clothType = clothTypeResolver.resolve(articleName);
        Article article = new Article(articleNumber, articleName, clothType);
        return articlesRepository.save(article);
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
