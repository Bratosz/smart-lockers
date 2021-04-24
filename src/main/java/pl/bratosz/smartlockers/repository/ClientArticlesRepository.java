package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.ClientArticle;

import java.util.List;

@Repository
public interface ClientArticlesRepository extends JpaRepository<ClientArticle, Long> {

    @Query("select a from ClientArticle a where a.client.id = :clientId")
    List<ClientArticle> getBy(
            @Param("clientId") long clientId);
}
