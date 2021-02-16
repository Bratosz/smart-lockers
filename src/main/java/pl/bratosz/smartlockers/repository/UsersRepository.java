package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.users.User;

@Repository
public interface UsersRepository extends JpaRepository <User, Long> {
    User getById(long id);
}
