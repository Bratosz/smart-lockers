package pl.bratosz.smartlockers.model.users;

import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.orders.OrderStatus;



import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class UserOurStaff extends User {

    @OneToMany(mappedBy = "user")
    private Set<Employee> employeesToManage;

    private Position position;

    public UserOurStaff() {
    }

    public UserOurStaff(OrderStatus.OrderStage initialStageForOrders,
                        String firstName,
                        String lastName,
                        String login,
                        String password,
                        String email,
                        Position position) {
        super(initialStageForOrders, firstName, lastName, login, password, email);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Set<Employee> getEmployeesToManage() {
        return employeesToManage;
    }

    public void setEmployeesToManage(Set<Employee> employeesToManage) {
        this.employeesToManage = employeesToManage;
    }

    public void add(Employee employeeToManage) {
        employeeToManage.setUserManaging(this);
        employeesToManage.add(employeeToManage);
    }
}
