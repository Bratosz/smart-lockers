package pl.bratosz.smartlockers.model;


import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Employee extends EmployeeGeneral {
    @JsonView(Views.DismissedEmployees.class)
    @ManyToMany(mappedBy = "dismissedEmployees")
    private List<Box> boxesOccupiedInPast;

    @JsonView({Views.InternalForEmployees.class, Views.InternalForBoxes.class})
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<Cloth> clothing;

    @JsonView(Views.InternalForEmployees.class)
    @OneToMany(mappedBy = "rotationOwner", cascade = CascadeType.ALL)
    private Set<Cloth> rotationalClothes;

    @JsonView({Views.InternalForEmployees.class, Views.InternalForBoxes.class})
    @OneToMany(mappedBy = "acceptedOwner", cascade = CascadeType.ALL)
    private Set<Cloth> acceptedClothes;

    @JsonView(Views.InternalForEmployees.class)
    @OneToMany(mappedBy = "withdrawnOwner", cascade = CascadeType.ALL)
    private Set<Cloth> withdrawnClothesz;

    @ManyToOne(cascade = CascadeType.ALL)
    private Department department;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private UserEmployee userEmployee;

    @JsonView(Views.InternalForBoxes.class)
    @OneToMany(mappedBy = "employee")
    private Set<ClothOrder> clothOrders;

    @JsonView(Views.Public.class)
    private boolean active;

    public Employee() {
    }

    public Employee(String firstName, String lastName, Department department, boolean active) {
        setFirstName(firstName);
        setLastName(lastName);
        setDepartment(department);
        setActive(active);
    }

    public Employee(boolean empty, String firstName, String lastName) {
//        setEmpty(empty);
//        setFirstName(firstName);
//        setLastName(lastName);
    }

    public List<Box> getBoxesOccupiedInPast() {
        return boxesOccupiedInPast;
    }

    public void setBoxesOccupiedInPast(List<Box> boxesOccupiedInPast) {
        this.boxesOccupiedInPast = boxesOccupiedInPast;
    }

    public Set<Cloth> getRotationalClothes() {
        return rotationalClothes;
    }

    public void setRotationalClothes(Set<Cloth> rotationalClothes) {
        this.rotationalClothes = rotationalClothes;
    }

    public Set<Cloth> getClothing() {
        return clothing;
    }

    public void setClothing(Set<Cloth> clothing) {
        this.clothing = clothing;
    }

    public Set<Cloth> getWithdrawnClothesz() {
        return withdrawnClothesz;
    }

    public void setWithdrawnClothesz(Set<Cloth> withdrawnClothesz) {
        this.withdrawnClothesz = withdrawnClothesz;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public UserEmployee getUserEmployee() {
        return userEmployee;
    }

    public void setUserEmployee(UserEmployee userEmployee) {
        this.userEmployee = userEmployee;
    }

    public Set<ClothOrder> getClothOrders() {
        return clothOrders;
    }

    public void setClothOrders(Set<ClothOrder> clothOrders) {
        this.clothOrders = clothOrders;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Cloth> getAcceptedClothes() {
        return acceptedClothes;
    }

    public void setAcceptedClothes(Set<Cloth> acceptedClothes) {
        this.acceptedClothes = acceptedClothes;
    }

    @Override
    public String toString() {
        return box.getLocker().getLockerNumber() + "/" + box.getBoxNumber() + " " +
                lastName + " " + firstName;
    }
}
