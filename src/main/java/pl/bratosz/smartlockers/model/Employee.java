package pl.bratosz.smartlockers.model;


import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Employee {
    @JsonView(Views.Public.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(Views.Public.class)
    private String firstName;

    @JsonView(Views.Public.class)
    private String lastName;

    @JsonView({Views.InternalForEmployees.class, Views.InternalForClothes.class})
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<Box> boxes;

    @JsonView(Views.DismissedEmployees.class)
    @ManyToMany(mappedBy = "dismissedEmployees")
    private List<Box> boxesOccupiedInPast;

    @JsonView(Views.InternalForEmployees.class)
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<RotationalCloth> rotationalClothing;

    @JsonView({Views.InternalForEmployees.class, Views.InternalForBoxes.class})
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<EmployeeCloth> clothing;

    @JsonView(Views.InternalForEmployees.class)
    @OneToMany
    private Set<EmployeeCloth> decommitedClothing;

    @ManyToOne(cascade = CascadeType.ALL)
    private Department department;

    public Employee() {
    }

    public Employee(String firstName, String lastName, Department department) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
    }


    public Set<RotationalCloth> getRotationalClothing() {
        return rotationalClothing;
    }

    public void setRotationalClothing(Set<RotationalCloth> rotationalClothing) {
        this.rotationalClothing = rotationalClothing;
    }

    public Set<EmployeeCloth> getClothing() {
        return clothing;
    }

    public void setClothing(Set<EmployeeCloth> clothing) {
        this.clothing = clothing;
    }

    public Long getId() {
        return id;
    }

    public List<Box> getBoxesOccupiedInPast() {
        return boxesOccupiedInPast;
    }

    public void setBoxesOccupiedInPast(List<Box> boxesOccupiedInPast) {
        this.boxesOccupiedInPast = boxesOccupiedInPast;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(Set<Box> boxes) {
        this.boxes = boxes;
    }

    public int getFirstLockerNumber() {
        if (getBoxes().size() == 1) {
            return getBoxes().stream().findFirst().get().getLocker().getLockerNumber();
        } else
            return 0;
    }

    public int getFirstBoxNumber() {
        if (getBoxes().size() >= 1) {
            return getBoxes().stream().findFirst().get().getBoxNumber();
        }
        return 0;
    }

    public int getFirstLockerPlantNumber() {
        if (getBoxes().size() >= 1) {
            return getBoxes().stream().findFirst().get().getLocker().getPlant().getPlantNumber();
        }
        return 0;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public boolean isEmployeeHaveThisBox(int lockerNo, int boxNo) {
        long count = boxes.stream()
                .filter(b -> b.getLocker().getLockerNumber() == lockerNo)
                .filter(b -> b.getBoxNumber() == boxNo)
                .count();
        if(count > 0) {
            return true;
        } else {
            return false;
        }
    }

}
