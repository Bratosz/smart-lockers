package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.orders.OrderStatus;
import pl.bratosz.smartlockers.model.users.ManagementList;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.model.users.UserOurStaff;
import pl.bratosz.smartlockers.repository.UsersOurStaffRepository;
import pl.bratosz.smartlockers.repository.UsersRepository;
import pl.bratosz.smartlockers.response.StandardResponse;

@Service
public class UserService {
    private UsersRepository usersRepository;
    private UsersOurStaffRepository usersOurStaffRepository;
    private EmployeeService employeeService;
    private ManagementListService managementListService;

    public UserService(UsersRepository usersRepository, UsersOurStaffRepository usersOurStaffRepository, EmployeeService employeeService, ManagementListService managementListService) {
        this.usersRepository = usersRepository;
        this.usersOurStaffRepository = usersOurStaffRepository;
        this.employeeService = employeeService;
        this.managementListService = managementListService;
    }

    public UserOurStaff create(String firstName, String lastName) {
        UserOurStaff user = new UserOurStaff();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setInitialStageForOrders(
                OrderStatus.OrderStage.ASSIGNED_AND_WAITING_FOR_RETURN);
        user.setManagementList(
                managementListService.create());
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
        Employee employee = employeeService.getById(employeeId);
        UserOurStaff user = usersOurStaffRepository.getById(userId);
        if(addToManagementList(employee, user)) {
            return new StandardResponse("Dodano do listy", true);
        } else {
            return new StandardResponse(
                    "Lista zawiera pracowników innego klienta. \n" +
                            "Żeby go dodać lista musi być pusta", false);
        }

    }

    private boolean addToManagementList(
            Employee employeeToManage,
            UserOurStaff user) {
        ManagementList managementList = user.getManagementList();
        long clientEmployeeId = employeeToManage.getDepartment().getClient().getId();
        if (managementList.getEmployees().isEmpty()) {
            managementList.addEmployee(employeeToManage);
            managementList.setActualClientId(clientEmployeeId);
            user.setManagementList(managementList);
            usersOurStaffRepository.save(user);
            return true;
        } else if (clientEmployeeId == managementList.getActualClientId()) {
            managementList.addEmployee(employeeToManage);
            user.setManagementList(managementList);
            usersOurStaffRepository.save(user);
            return true;
        } else {
            return false;
        }
    }
}
