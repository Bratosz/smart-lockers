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

    @JsonView(Views.Public.class)
    @OneToOne
    private ExchangeStatus exchangeStatus;

    public EmployeeCloth(){}

    public EmployeeCloth(long id, Date assignment, Date lastWashing, Date release, int ordinalNo,
                         int articleNo, Employee employee, boolean acceptedForExchange,
                         ExchangeStatus exchangeStatus, Size size) {
        super(id, assignment, lastWashing, release, ordinalNo, articleNo, size);
        this.employee = employee;
        this.acceptedForExchange = acceptedForExchange;
        this.exchangeStatus = exchangeStatus;
    }

    public EmployeeCloth(long id, Date assignment, Date lastWashing, Date release,
                         int ordinalNo, int articleNo, Size size) {
        super(id, assignment, lastWashing, release, ordinalNo, articleNo, size);
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

    public ExchangeStatus getExchangeStatus() {
        return exchangeStatus;
    }

    public void setExchangeStatus(ExchangeStatus exchangeStatus) {
        this.exchangeStatus = exchangeStatus;
    }
}
