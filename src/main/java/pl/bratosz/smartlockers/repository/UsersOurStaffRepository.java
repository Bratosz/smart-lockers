package pl.bratosz.smartlockers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bratosz.smartlockers.model.users.UserOurStaff;

@Repository
public interface UsersOurStaffRepository extends JpaRepository<UserOurStaff, Long> {

    UserOurStaff getById(long userId);
}
