package pl.bratosz.smartlockers.service.managers;

import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.EmployeeGeneral;
import pl.bratosz.smartlockers.model.users.User;

import java.util.Date;

import static pl.bratosz.smartlockers.model.Box.BoxStatus.*;

//Manager should be created before each operation

public class BoxManager {
    private User user;
    private Date date;
    private Box box;

    public BoxManager(User user) {
        this.user = user;
        this.date = new Date();
    }

    public BoxManager(User user, Date date) {
        this.user = user;
        this.date = date;
    }

    public Box release(Box box) {
        EmployeeGeneral releasedEmployee = box.getEmployee();
        if(employeeIsPresent(releasedEmployee)) {
            box.setBoxStatus(FREE);
            box.setEmployee(setDummy());
            box.getReleasedEmployees().add((Employee) releasedEmployee);
            return box;
        } else {
            return box;
        }
    }

    public EmployeeGeneral extractEmployee(Box box) {
        EmployeeGeneral employee = box.getEmployee();
        if(employeeIsPresent(employee)) {
            employee.setBox(null);
            return employee;
        } else {
            return employee;
        }
    }

    private EmployeeGeneral setDummy() {
        return box.getEmployeeDummy();
    }



    private boolean employeeIsPresent(EmployeeGeneral employee) {
        if(employee.getClass().isInstance(Employee.class)) {
            return true;
        } else {
            return false;
        }
    }
}
