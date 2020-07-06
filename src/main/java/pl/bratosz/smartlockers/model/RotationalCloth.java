package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.exception.NotReleasedClothException;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import java.util.Date;

import static pl.bratosz.smartlockers.model.Views.*;

@Entity
public class RotationalCloth extends Cloth {
    @JsonView(Public.class)
    private Date releasedToEmployee;

    @JsonView(InternalForClothes.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Employee employee;

    @JsonView(Public.class)
    private boolean isReturned;

    public RotationalCloth() {
    }

    public RotationalCloth(long id, Date assignment, Date lastWashing, Date release,
                           int ordinalNo, Article article, Date releasedToEmployee,
                           Employee employee, boolean isReturned, Size size) {
        super(id, assignment, lastWashing, release, ordinalNo, article, size);
        this.releasedToEmployee = releasedToEmployee;
        this.employee = employee;
        this.isReturned = isReturned;
    }

    @Override
    public void setLastWashing(Date date) {
        if (this.isReturned) {
            this.lastWashing = date;
        } else {
            this.isReturned = true;
            this.lastWashing = date;
        }
    }

    public void setReleasedToEmployee(Date date) {
        this.releasedToEmployee = date;
        setReturned(isClothReturned());
    }

    private boolean isClothReturned() {
        return releasedToEmployee.compareTo(lastWashing) < 0;
    }

    public Employee getEmployee() {
        if (!isReturned) {
            return employee;
        } else {
            return null;
        }
    }

    public void setEmployee(Employee employee) {
        if (isReturned) return;
        this.employee = employee;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean isReturned) {
        if (isReturned) {
            this.isReturned = true;
            setEmployee(null);
        } else {
            this.isReturned = false;
        }
    }
}
