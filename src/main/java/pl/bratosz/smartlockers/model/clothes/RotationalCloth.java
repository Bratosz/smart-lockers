package pl.bratosz.smartlockers.model.clothes;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.model.clothes.Cloth;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class RotationalCloth extends Cloth {
    @JsonView(Views.Public.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Employee rotationTemporaryOwner;

    @JsonView(Views.Public.class)
    private Date releasedToEmployee;

    @JsonView(Views.Public.class)
    private boolean returned;

    public RotationalCloth() {
    }

    public Employee getRotationTemporaryOwner() {
        return rotationTemporaryOwner;
    }

    public void setRotationTemporaryOwner(Employee rotationTemporaryOwner) {
        this.rotationTemporaryOwner = rotationTemporaryOwner;
    }

    public Date getReleasedToEmployee() {
        return releasedToEmployee;
    }

    public void setReleasedToEmployee(Date releasedToEmployee) {
        this.releasedToEmployee = releasedToEmployee;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
