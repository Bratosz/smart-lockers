package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.List;

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

    @JsonView({Views.InternalForLockers.class, Views.InternalForBoxes.class})
    @OneToOne(cascade = CascadeType.ALL)
    private EmployeeGeneral employee;

    @OneToOne(cascade = CascadeType.ALL)
    private EmployeeDummy employeeDummy;

    @JsonView(Views.DismissedEmployees.class)
    @ManyToMany
    @JoinTable(
            name = "dismissed_employees",
            joinColumns = @JoinColumn(name = "box_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id"))
    private List<Employee> dismissedEmployees;

    @JsonView({Views.InternalForEmployees.class, Views.InternalForBoxes.class, Views.InternalForClothes.class})
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="locker_id")
    private Locker locker;

    public Box() {
    }

    public Box(int boxNumber) {
        this.boxNumber = boxNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(int boxNumber) {
        this.boxNumber = boxNumber;
    }

    public BoxStatus getBoxStatus() {
        return boxStatus;
    }

    public void setBoxStatus(BoxStatus boxStatus) {
        this.boxStatus = boxStatus;
    }

    public EmployeeGeneral getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeGeneral employee) {
        if(employee.isDummy()) {
            this.employee = employee;
            setBoxStatus(BoxStatus.FREE);
        } else {
            this.employee = employee;
            setBoxStatus(BoxStatus.OCCUPY);
        }
    }

    public EmployeeDummy getEmployeeDummy() {
        return employeeDummy;
    }

    public void setEmployeeDummy(EmployeeDummy employeeDummy) {
        this.employeeDummy = employeeDummy;
    }

    public List<Employee> getDismissedEmployees() {
        return dismissedEmployees;
    }

    public void setDismissedEmployees(List<Employee> dismissedEmployees) {
        this.dismissedEmployees = dismissedEmployees;
    }

    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
    }

//    public void setEmployee(Employee employee) {
//        if(employee.isEmpty()) {
//            this.employee = employee;
//            setBoxStatus(BoxStatus.FREE);
//        } else {
//            setEmptyEmployee(this.employee);
//            this.employee = employee;
//            setBoxStatus(BoxStatus.OCCUPY);
//        }
//    }



    public enum BoxStatus {
        OCCUPY("Zajęta"),
        FREE("Wolna"),
        UNDEFINED("Niezdefiniowana"),
        DAMAGED("Uszkodzona");

        private String name;


        BoxStatus(String name) {
            this.name = name;
        }

        @JsonValue
        public String getName() {
            return name;
        }
    }



//    private void setEmptyEmployee(Employee emptyEmployee) {
//        if(emptyEmployee.isEmpty()) {
//            this.emptyEmployee = emptyEmployee;
//        } else {
//            throw new InvalidEmployeeException("Empty employee is not empty");
//        }
//    }
}
