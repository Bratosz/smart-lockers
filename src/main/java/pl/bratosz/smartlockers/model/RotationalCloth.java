package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import java.util.Date;

@Entity
public class RotationalCloth extends Cloth {
    @JsonView(Views.InternalForClothes.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Employee employee;

    public RotationalCloth(){}

    public RotationalCloth(long id, Date lastWashing, int ordinalNo, int articleNo) {
        super(id, lastWashing, ordinalNo, articleNo);
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
