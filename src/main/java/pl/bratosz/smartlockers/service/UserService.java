package pl.bratosz.smartlockers.service;


import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Permissions;
import pl.bratosz.smartlockers.model.User;
import pl.bratosz.smartlockers.model.UserOurStaff;
import pl.bratosz.smartlockers.repository.UsersOurStaffRepository;
import pl.bratosz.smartlockers.repository.UsersRepository;

@Service
public class UserService {
    private UsersRepository usersRepository;
    private UsersOurStaffRepository usersOurStaffRepository;

    public UserService(UsersRepository usersRepository, UsersOurStaffRepository usersOurStaffRepository) {
        this.usersRepository = usersRepository;
        this.usersOurStaffRepository = usersOurStaffRepository;
    }


    public User getById(long userId) {
        return usersRepository.getOne(userId);
    }

    public UserOurStaff create(String firstName, String lastName, Permissions permissions) {
        UserOurStaff user = new UserOurStaff();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPermissions(permissions);
        return usersRepository.save(user);
    }

    public User getUserById(long id) {
        return usersRepository.getById(id);
    }

    public UserOurStaff getOurUserById(long id) {
        return usersOurStaffRepository.getById(id);
    }

}
