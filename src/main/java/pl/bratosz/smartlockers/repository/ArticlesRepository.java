package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.Article;

@Repository
public interface ArticlesRepository extends JpaRepository <Article, Long> {

    @Query("select a from Article a where a.articleNumber = :articleNumber")
    Article getByArticleNumber(int articleNumber);
}
