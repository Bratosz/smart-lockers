package pl.bratosz.smartlockers.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class UserClient extends User {

    @OneToMany(mappedBy = "confirmedBy", cascade = CascadeType.ALL)
    private Set<ClothOrder> confirmedOrders;

    @ManyToOne(cascade = CascadeType.ALL)
    private Plant plant;

    @ManyToOne(cascade = CascadeType.ALL)
    private Department department;

    public UserClient(){
    }

    public UserClient(String firstName, String lastName, String login, String password,
                      String email, Permissions permissions, Plant plant, Department department) {
        super(firstName, lastName, login, password, email, permissions);
        this.plant = plant;
        this.department = department;
    }

    public Set<ClothOrder> getConfirmedOrders() {
        return confirmedOrders;
    }

    public void setConfirmedOrders(Set<ClothOrder> confirmedOrders) {
        this.confirmedOrders = confirmedOrders;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
