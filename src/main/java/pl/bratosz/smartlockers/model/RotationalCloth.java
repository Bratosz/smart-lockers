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
    @JsonView(InternalForClothes.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Employee employee;

    @JsonView(Public.class)
    private boolean isReturned;

    public RotationalCloth() {
    }

    public RotationalCloth(long id, Date lastWashing, int ordinalNo, int articleNo) {
        super(id, lastWashing, ordinalNo, articleNo);
        isReturned = true;
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

    @Override
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
