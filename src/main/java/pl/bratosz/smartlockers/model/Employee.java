package pl.bratosz.smartlockers.model;


import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.RotationalCloth;
import pl.bratosz.smartlockers.model.users.UserEmployee;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Employee extends EmployeeGeneral {

    @JsonView({Views.InternalForEmployees.class, Views.InternalForBoxes.class})
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<Cloth> clothes;

    @JsonView(Views.InternalForEmployees.class)
    @OneToMany(mappedBy = "rotationTemporaryOwner", cascade = CascadeType.ALL)
    private Set<RotationalCloth> rotationalClothes;

    @ManyToOne(cascade = CascadeType.ALL)
    private Department department;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private UserEmployee userEmployee;

    @JsonView(Views.Public.class)
    private boolean active;

    @JsonView(Views.Public.class)
    private String note;

    @ManyToMany(mappedBy = "releasedEmployees", fetch = FetchType.LAZY)
    private List<Box> pastBoxes;

    public Employee() {
    }

    public Employee(String firstName, String lastName, Department department, boolean active) {
        setFirstName(firstName);
        setLastName(lastName);
        setDepartment(department);
        setActive(active);
    }

    public Set<RotationalCloth> getRotationalClothes() {
        return rotationalClothes;
    }

    public void setRotationalClothes(Set<RotationalCloth> rotationalClothes) {
        this.rotationalClothes = rotationalClothes;
    }

    public Set<Cloth> getClothes() {
        return clothes;
    }

    public void setClothes(Set<Cloth> clothes) {
        this.clothes = clothes;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Box> getPastBoxes() {
        return pastBoxes;
    }

    public void setPastBoxes(List<Box> pastBoxes) {
        this.pastBoxes = pastBoxes;
    }

    public void setAsPastBox(Box box) {
        pastBoxes.add(box);
        this.box = null;
    }

    public Box getLastBox() {
        int last = pastBoxes.size() - 1;
        return pastBoxes.get(last);
    }

    @Override
    public String toString() {
        return box.getLocker().getLockerNumber() + "/" + box.getBoxNumber() + " " +
                lastName + " " + firstName;
    }
}
