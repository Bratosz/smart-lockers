package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Date;

@Entity
public class EmployeeCloth extends Cloth {
    @JsonView(Views.InternalForClothes.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Employee employee;

    @JsonView(Views.Public.class)
    private boolean acceptedForExchange;


    public EmployeeCloth(){}

    public EmployeeCloth(long id, Date assignment, Date lastWashing, Date release, int ordinalNo,
                         Article article, Employee employee, boolean acceptedForExchange, Size size) {
        super(id, assignment, lastWashing, release, ordinalNo, article, size);
        this.employee = employee;
        this.acceptedForExchange = acceptedForExchange;
    }

    public EmployeeCloth(long id, Date assignment, Date lastWashing, Date release,
                         int ordinalNo, Article article, Size size) {
        super(id, assignment, lastWashing, release, ordinalNo, article, size);
        acceptedForExchange = false;
    }


    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public boolean isAcceptedForExchange() {
        return acceptedForExchange;
    }

    public void setAcceptedForExchange(boolean acceptedForExchange) {
        this.acceptedForExchange = acceptedForExchange;
    }
}
