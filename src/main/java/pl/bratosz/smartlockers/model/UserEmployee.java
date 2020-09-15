package pl.bratosz.smartlockers.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Set;

@Entity
public class UserEmployee extends User {

    @OneToOne(cascade = CascadeType.ALL)
    private Employee employee;

    @OneToMany(mappedBy = "requestedBy")
    private Set<ClothOrder> requestedOrders;

    public UserEmployee(){}

    public UserEmployee(String firstName, String lastName, String login,
                        String password, String email, Permissions permissions, Employee employee) {
        super(firstName, lastName, login, password, email, permissions);
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Set<ClothOrder> getRequestedOrders() {
        return requestedOrders;
    }

    public void setRequestedOrders(Set<ClothOrder> requestedOrders) {
        this.requestedOrders = requestedOrders;
    }
}
