package pl.bratosz.smartlockers.model.clothes;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothActualStatus;
import pl.bratosz.smartlockers.model.clothes.ClothDestination;
import pl.bratosz.smartlockers.model.users.User;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ClothStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonView({Views.InternalForEmployees.class, Views.InternalForBoxes.class})
    private ClothActualStatus status;
    @JsonView({Views.InternalForEmployees.class, Views.InternalForBoxes.class})
    private ClothDestination clothDestination;
    @ManyToOne
    private User user;
    private Date dateOfUpdate;
    @ManyToOne
    private Cloth cloth;

    public ClothStatus() {
    }

    public ClothStatus(ClothActualStatus status,
                       ClothDestination clothDestination,
                       Cloth cloth,
                       User user,
                       Date dateOfUpdate) {
        this.status = status;
        this.clothDestination = clothDestination;
        this.cloth = cloth;
        this.user = user;
        this.dateOfUpdate = dateOfUpdate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ClothDestination getClothDestination() {
        return clothDestination;
    }

    public void setClothDestination(ClothDestination clothDestination) {
        this.clothDestination = clothDestination;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDateOfUpdate() {
        return dateOfUpdate;
    }

    public void setDateOfUpdate(Date dateOfUpdate) {
        this.dateOfUpdate = dateOfUpdate;
    }

    public Cloth getCloth() {
        return cloth;
    }

    public void setCloth(Cloth cloth) {
        this.cloth = cloth;
    }

    public ClothActualStatus getStatus() {
        return status;
    }

    public void setStatus(ClothActualStatus status) {
        this.status = status;
    }


}
