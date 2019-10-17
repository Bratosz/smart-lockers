package pl.bratosz.smartlockers.model;

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
    private Integer boxNumber;

    @JsonView(Views.Public.class)
    @Enumerated(EnumType.STRING)
    private BoxStatus boxStatus;

    @JsonView({Views.InternalForLockers.class, Views.InternalForBoxes.class})
    @ManyToOne(cascade = CascadeType.ALL)
    private Employee employee;

    private Long emptyBoxEmployeeNo;

    @JsonView(Views.DismissedEmployees.class)
    @ManyToMany
    @JoinTable(
            name = "dismissed_employees",
            joinColumns = @JoinColumn(name = "box_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id"))
    private List<Employee> dismissedEmployees;

    @JsonView({Views.InternalForEmployees.class, Views.InternalForBoxes.class})
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "locker_id", insertable = false, updatable = false)
    private Locker locker;

    public Box() {
    }

    public Box(int boxNumber, BoxStatus boxStatus) {
        this.boxNumber = boxNumber;
        this.boxStatus = boxStatus;
    }

    public Box(int boxNumber, BoxStatus boxStatus, Long emptyEmployeeNo) {
        this.boxNumber = boxNumber;
        this.boxStatus = boxStatus;
        this.emptyBoxEmployeeNo = emptyEmployeeNo;
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

    public Integer getBoxNumber() {
        return boxNumber;
    }

    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
    }

    public List<Employee> getDismissedEmployees() {
        return dismissedEmployees;
    }

    public void setDismissedEmployees(List<Employee> dismissedEmployees) {
        this.dismissedEmployees = dismissedEmployees;
    }

    public Long getEmptyBoxEmployeeNo() {
        return emptyBoxEmployeeNo;
    }

    public void setEmptyBoxEmployeeNo(Long emptyBoxEmployeeNo) {
        this.emptyBoxEmployeeNo = emptyBoxEmployeeNo;
    }

    public enum BoxStatus {
        OCCUPY("Zajęta"),
        FREE("Wolna"),
        UNDEFINED("Niezdefiniowana"),
        DAMAGED("Uszkodzona");

        private String name;

        BoxStatus(String name) {
            this.name = name;
        }
    }

}
