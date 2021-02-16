package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.model.users.UserOurStaff;
import pl.bratosz.smartlockers.model.users.roles.StaffServiceman;
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

    public UserOurStaff create(String firstName, String lastName) {
        UserOurStaff user = new UserOurStaff();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserRole(new StaffServiceman());
        return usersRepository.save(user);
    }

    public User getUserById(long id) {
        return usersRepository.getById(id);
    }

    public UserOurStaff getOurUserById(long id) {
        return usersOurStaffRepository.getById(id);
    }

}
