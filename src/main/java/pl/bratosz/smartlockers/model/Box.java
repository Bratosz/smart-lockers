package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

@Entity
public class Box {
    @JsonView(Views.Public.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(Views.Public.class)
    private int boxNumber;

    @JsonView(Views.Public.class)
    @Enumerated(EnumType.STRING)
    private BoxStatus boxStatus;

    @JsonView(Views.InternalForLockers.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Employee employee;

    @JsonView(Views.InternalForEmployees.class)
    @ManyToOne
    @JoinColumn(name = "locker_id", insertable = false, updatable = false)
    private Locker locker;

    public Box() {
    }

    public Box(int boxNumber, BoxStatus boxStatus) {
        this.boxNumber = boxNumber;
        this.boxStatus = boxStatus;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public BoxStatus getBoxStatus() {
        return boxStatus;
    }

    public void setBoxStatus(BoxStatus boxStatus) {
        this.boxStatus = boxStatus;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBoxNumber(int boxNumber) {
        this.boxNumber = boxNumber;
    }

    public Long getId() {
        return id;
    }

    public int getBoxNumber() {
        return boxNumber;
    }

    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
    }

    public enum BoxStatus {
        OCCUPY("Zajęta"),
        FREE("Wolna"),
        DAMAGED("Uszkodzona");

        private String name;

        BoxStatus(String name) {
            this.name = name;
        }
    }

}
