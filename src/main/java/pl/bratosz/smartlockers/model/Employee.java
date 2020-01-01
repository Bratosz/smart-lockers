package pl.bratosz.smartlockers.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.sql.Update;
import pl.bratosz.smartlockers.validators.annotations.CorrectFirstName;

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
    @CorrectFirstName
    private String firstName;

    @JsonView(Views.Public.class)
    private String lastName;

    @JsonView(Views.Public.class)
    @Enumerated(EnumType.STRING)
    private Department department;

    @JsonView(Views.InternalForEmployees.class)
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<Box> boxes;

    @JsonView(Views.DismissedEmployees.class)
    @ManyToMany(mappedBy = "dismissedEmployees")
    private List<Box> boxesOccupiedInPast;


    public Employee() {
    }

    public Employee(String firstName, String lastName, Department department) throws ConstraintViolationException {
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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
        }
        return 0;
    }

    public int getFirstBoxNumber() {
        if (getBoxes().size() == 1) {
            return getBoxes().stream().findFirst().get().getBoxNumber();
        }
        return 0;
    }

    public Locker.DepartmentNumber getFirstLockerDepartmentNumber() {
        if (getBoxes().size() == 1) {
            return getBoxes().stream().findFirst().get().getLocker().getDepartmentNumber();
        }
        return Locker.DepartmentNumber.DEP_000;
    }
}
