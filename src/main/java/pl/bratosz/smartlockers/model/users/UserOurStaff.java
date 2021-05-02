package pl.bratosz.smartlockers.model.users;

import pl.bratosz.smartlockers.model.orders.OrderStatus;



import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class UserOurStaff extends User {

    @OneToOne
    private ManagementList managementList;

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

    public ManagementList getManagementList() {
        return managementList;
    }

    public void setManagementList(ManagementList managementList) {
        managementList.setManagingUser(this);
        this.managementList = managementList;
    }
}
