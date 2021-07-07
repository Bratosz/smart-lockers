package pl.bratosz.smartlockers.model;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class SimpleEmployee {
    private String firstName;
    private String lastName;
    private int locker;
    private int box;
    private String departmentAlias;

    public SimpleEmployee(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public SimpleEmployee(String firstName,
                          String lastName,
                          int locker,
                          int box) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.locker = locker;
        this.box = box;
    }

    public SimpleEmployee(String firstName,
                          String lastName,
                          int locker,
                          int box,
                          String departmentAlias) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.locker = locker;
        this.box = box;
        this.departmentAlias = departmentAlias;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getLockerNumber() {
        return locker;
    }

    public void setLocker(int locker) {
        this.locker = locker;
    }

    public int getBoxNumber() {
        return box;
    }

    public void setBox(int box) {
        this.box = box;
    }

    public String getDepartmentAlias() {
        return departmentAlias;
    }

    public void setDepartmentAlias(String departmentAlias) {
        this.departmentAlias = departmentAlias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleEmployee that = (SimpleEmployee) o;
        return getLockerNumber() == that.getLockerNumber() &&
                getBoxNumber() == that.getBoxNumber() &&
                Objects.equals(getFirstName(), that.getFirstName()) &&
                Objects.equals(getLastName(), that.getLastName()) &&
                Objects.equals(getDepartmentAlias(), that.getDepartmentAlias());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getLockerNumber(), getBoxNumber(), getDepartmentAlias());
    }
}
