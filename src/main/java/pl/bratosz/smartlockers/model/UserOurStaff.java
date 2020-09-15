package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class UserOurStaff extends User {

    @JsonView(Views.Public.class)
    private Position position;

    @OneToMany(mappedBy = "acceptedBy", cascade = CascadeType.ALL)
    private Set<ClothOrder> acceptedOrders;

    @OneToMany(mappedBy = "inRealizationBy")
    private Set<ClothOrder> inRealizationOrders;

    @OneToMany(mappedBy = "preparedBy")
    private Set<ClothOrder> preparedOrders;

    @OneToMany(mappedBy = "defferedBy")
    private Set<ClothOrder> defferedOrders;

    public UserOurStaff(){
    }

    public UserOurStaff(String firstName, String lastName, String login, String password, String email, Permissions permissions, Position position) {
        super(firstName, lastName, login, password, email, permissions);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Set<ClothOrder> getAcceptedOrders() {
        return acceptedOrders;
    }

    public void setAcceptedOrders(Set<ClothOrder> acceptedOrders) {
        this.acceptedOrders = acceptedOrders;
    }

    public Set<ClothOrder> getInRealizationOrders() {
        return inRealizationOrders;
    }

    public void setInRealizationOrders(Set<ClothOrder> inRealizationOrders) {
        this.inRealizationOrders = inRealizationOrders;
    }

    public Set<ClothOrder> getPreparedOrders() {
        return preparedOrders;
    }

    public void setPreparedOrders(Set<ClothOrder> preparedOrders) {
        this.preparedOrders = preparedOrders;
    }

    public Set<ClothOrder> getDefferedOrders() {
        return defferedOrders;
    }

    public void setDefferedOrders(Set<ClothOrder> defferedOrders) {
        this.defferedOrders = defferedOrders;
    }
}
