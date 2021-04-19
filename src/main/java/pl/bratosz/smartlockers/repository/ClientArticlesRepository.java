package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.ClientArticle;

@Repository
public interface ClientArticlesRepository extends JpaRepository<ClientArticle, Long> {
}
