package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.clothes.Article;

@Repository
public interface ArticlesRepository extends JpaRepository <Article, Long> {

    @Query("select a from Article a where a.number = :number")
    Article getBy(@Param("number")int number);
}
