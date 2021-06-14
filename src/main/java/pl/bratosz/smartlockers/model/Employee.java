package pl.bratosz.smartlockers.model;


import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.RotationalCloth;
import pl.bratosz.smartlockers.model.orders.ClothOrder;
import pl.bratosz.smartlockers.model.users.UserEmployee;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Entity
public class Employee extends EmployeeGeneral {

    @JsonView({Views.EmployeeCompleteInfo.class})
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Cloth> clothes;

    @JsonView({Views.EmployeeCompleteInfo.class})
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<ClothOrder> clothOrders;

    @JsonView(Views.EmployeeCompleteInfo.class)
    @OneToMany(mappedBy = "rotationTemporaryOwner", cascade = CascadeType.ALL)
    private Set<RotationalCloth> rotationalClothes;

    @JsonView(Views.EmployeeCompleteInfo.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Department department;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private UserEmployee userEmployee;

    @JsonView(Views.Public.class)
    private boolean active;

    @JsonView(Views.EmployeeCompleteInfo.class)
    private String note;

    @JsonView(Views.EmployeeCompleteInfo.class)
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<SimpleBox> pastBoxes;

    @JsonView(Views.InternalForEmployeesForOurStaff.class)
    private Double redemptionPrice;


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

    public List<Cloth> getClothes() {
        return clothes;
    }

    public void setClothes(List<Cloth> clothes) {
        this.clothes = clothes;
    }

    public void addClothes(List<Cloth> clothes) {
        for(Cloth c : clothes) {
            c.setEmployee(this);
        }
        this.setClothes(clothes);
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

    public List<SimpleBox> getPastBoxes() {
        return pastBoxes;
    }

    public void setPastBoxes(List<SimpleBox> pastBoxes) {
        this.pastBoxes = pastBoxes;
    }

    public void setAsPastBox(SimpleBox box) {
        if(pastBoxes == null) {
            pastBoxes = new LinkedList<>();
        }
        pastBoxes.add(box);
    }

    public SimpleBox getLastBox() {
        int last = pastBoxes.size() - 1;
        return pastBoxes.get(last);
    }

    @Override
    public String toString() {
        return box.getLocker().getLockerNumber() + "/" + box.getBoxNumber() + " " +
                lastName + " " + firstName;
    }

    public Double getRedemptionPrice() {
        return redemptionPrice;
    }

    public void setRedemptionPrice(Double redemptionPrice) {
        this.redemptionPrice = redemptionPrice;
    }

    public List<ClothOrder> getClothOrders() {
        return clothOrders;
    }

    public void setClothOrders(List<ClothOrder> clothOrders) {
        this.clothOrders = clothOrders;
    }
}
