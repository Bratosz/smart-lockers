package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

@Entity
public class EmployeeDummy extends EmployeeGeneral {
    @JsonView({Views.InternalForEmployees.class, Views.InternalForClothes.class})
    @OneToOne(mappedBy = "employeeDummy", cascade = CascadeType.ALL)
    private Box dummyBox;

    public EmployeeDummy(){
    }

    public EmployeeDummy(Box box){
        firstName = "";
        lastName = "";
        this.box = box;
        this.dummyBox = this.box;
        this.dummy = true;
    }

    public Box getDummyBox() {
        return dummyBox;
    }

    public void setDummyBox(Box dummyBox) {
        this.dummyBox = dummyBox;
    }
}
