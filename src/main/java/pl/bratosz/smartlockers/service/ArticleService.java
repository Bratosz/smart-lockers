package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Article;
import pl.bratosz.smartlockers.repository.ArticlesRepository;

@Service
public class ArticleService {
    private ArticlesRepository articlesRepository;

    public ArticleService(ArticlesRepository articlesRepository) {
        this.articlesRepository = articlesRepository;
    }

    public Article getByArticleNumber(int articleNumber) {
        return articlesRepository.getByArticleNumber(articleNumber);
    }
}
