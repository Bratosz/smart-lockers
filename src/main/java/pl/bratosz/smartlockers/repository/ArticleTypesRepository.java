package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.clothes.ArticleType;

@Repository
public interface ArticleTypesRepository extends JpaRepository <ArticleType, Long> {

    @Query("select a from ArticleType a where a.articleNumber = :articleNumber")
    ArticleType getByArticleTypeNumber(int articleNumber);
}
