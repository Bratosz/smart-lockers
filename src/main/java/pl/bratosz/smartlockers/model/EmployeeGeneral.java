package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.strings.MyString;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class EmployeeGeneral {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Public.class)
    protected long id;
    @JsonView(Views.Public.class)
    protected String firstName;
    @JsonView(Views.Public.class)
    protected String lastName;
    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonView({Views.InternalForEmployees.class, Views.InternalForClothes.class})
    protected Box box;

    protected  boolean dummy;

    public EmployeeGeneral(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return MyString.create(lastName).get();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }

   public void addToBox(Box box) {
        box.setEmployee(this);
        this.setBox(box);
   }

    public boolean isDummy() {
        return dummy;
    }

    private void setDummy(boolean dummy) {
        this.dummy = dummy;
    }
}
