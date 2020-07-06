package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;


@Entity
public class Locker {
    @JsonView(Views.Public.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(Views.Public.class)
    private int lockerNumber;

    @JsonView(Views.Public.class)
    private Integer capacity;

    @JsonView(Views.Public.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Department department;

    @JsonView(Views.Public.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Location location;

    @JsonView(Views.InternalForLockers.class)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "locker_id")
    private List<Box> boxes;

    @ManyToOne(cascade = CascadeType.ALL)
    private Plant plant;

    public Locker() {
    }

    public Locker(int lockerNumber, Integer capacity,
                  Department department, Location location,
                  List<Box> boxes, Plant plant) {
        this.lockerNumber = lockerNumber;
        this.capacity = capacity;
        this.department = department;
        this.location = location;
        this.boxes = boxes;
        this.plant = plant;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLockerNumber() {
        return lockerNumber;
    }

    public void setLockerNumber(int lockerNumber) {
        this.lockerNumber = lockerNumber;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Optional<Box> getBoxByNumber(Integer boxNumber) {
        return boxes.stream().filter(x -> x.getBoxNumber() == boxNumber).findFirst();
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }
}
