package pl.bratosz.smartlockers.model.users;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.calculator.CalcCloth;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.SimpleEmployee;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.model.clothes.Cloth;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class ManagementList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long actualClientId;

    @OneToMany
    @JsonView(Views.InternalForEmployeesForOurStaff.class)
    private Set<Employee> employees;

    @Embedded
    private List<SimpleEmployee> missedEmployees;

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

    public List<SimpleEmployee> getMissedEmployees() {
        return missedEmployees;
    }

    public void setMissedEmployees(List<SimpleEmployee> missedEmployees) {
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

    public ManagementList calculateRedemptionPrices() {
        Set<Employee> employeesWithPrices = getEmployees().stream().map(employee -> {
            employee.getClothes()
                    .stream()
                    .forEach(cloth -> cloth.setActualRedemptionPrice(
                            CalcCloth.calculateRedemptionPrice(cloth)));
            return sumRedemptionPricesFromClothes(employee);
        }).collect(Collectors.toSet());
        setEmployees(employeesWithPrices);
        return this;
    }

    private Employee sumRedemptionPricesFromClothes(Employee employee) {
        float redemptionPrice = 0;
        List<Cloth> clothes = employee.getClothes();
        for(Cloth c : clothes)
            redemptionPrice = redemptionPrice + c.getActualRedemptionPrice().floatValue();
        employee.setRedemptionPrice(BigDecimal.valueOf(redemptionPrice));
        return employee;
    }
}
