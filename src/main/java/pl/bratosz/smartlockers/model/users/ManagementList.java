package pl.bratosz.smartlockers.model.users;

import pl.bratosz.smartlockers.model.Employee;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class ManagementList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long actualClientId;

    @OneToMany
    private Set<Employee> employees;

    private List<String> missedEmployees;

    @OneToOne(mappedBy = "managementList")
    private UserOurStaff managingUser;

    public ManagementList() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    public List<String> getMissedEmployees() {
        return missedEmployees;
    }

    public void setMissedEmployees(List<String> missedEmployees) {
        this.missedEmployees = missedEmployees;
    }

    public UserOurStaff getManagingUser() {
        return managingUser;
    }

    public void setManagingUser(UserOurStaff managingUser) {
        this.managingUser = managingUser;
    }

    public long getActualClientId() {
        return actualClientId;
    }

    public void setActualClientId(long actualClientId) {
        this.actualClientId = actualClientId;
    }

    public void addEmployee(Employee employeeToManage) {
        employees.add(employeeToManage);
    }
}
