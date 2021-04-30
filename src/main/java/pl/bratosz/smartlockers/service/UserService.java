package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.orders.OrderStatus;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.model.users.UserOurStaff;
import pl.bratosz.smartlockers.repository.UsersOurStaffRepository;
import pl.bratosz.smartlockers.repository.UsersRepository;
import pl.bratosz.smartlockers.response.StandardResponse;

import java.util.HashSet;

@Service
public class UserService {
    private UsersRepository usersRepository;
    private UsersOurStaffRepository usersOurStaffRepository;
    private EmployeeService employeeService;

    public UserService(UsersRepository usersRepository, UsersOurStaffRepository usersOurStaffRepository, EmployeeService employeeService) {
        this.usersRepository = usersRepository;
        this.usersOurStaffRepository = usersOurStaffRepository;
        this.employeeService = employeeService;
    }

    public UserOurStaff create(String firstName, String lastName) {
        UserOurStaff user = new UserOurStaff();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmployeesToManage(new HashSet<>());
        user.setInitialStageForOrders(OrderStatus.OrderStage.ASSIGNED_AND_WAITING_FOR_RETURN);
        return usersRepository.save(user);
    }

    public User getUserById(long id) {
        return usersRepository.getById(id);
    }

    public UserOurStaff getOurUserById(long id) {
        return usersOurStaffRepository.getById(id);
    }

    public User getDefaultUser() {
        User defaultUser = usersRepository.getUserByFirstName("default");
        if (defaultUser == null) {
            return create("default", "user");
        } else {
            return defaultUser;
        }
    }

    public StandardResponse addEmployeeToManagementList(
            long employeeId,
            long userId) {
        Employee employee = employeeService.getBy(employeeId);
        UserOurStaff user = usersOurStaffRepository.getById(userId);
        addToManagementList(employee, user);

    }

    private boolean addToManagementList(
            Employee employeeToManage,
            UserOurStaff user) {
        if (user.getEmployeesToManage().isEmpty()) {
            user.add(employeeToManage);
            usersOurStaffRepository.save(user);
            return true;
        } else if ()
        }
    }
}
