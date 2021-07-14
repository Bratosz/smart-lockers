package pl.bratosz.smartlockers.model;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class SimpleEmployee {
    private String firstName;
    private String lastName;
    private int lockerNumber;
    private int boxNumber;
    private String departmentAlias;
    private String comment;

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
        this.lockerNumber = locker;
        this.boxNumber = box;
    }

    public SimpleEmployee(String firstName,
                          String lastName,
                          int locker,
                          int box,
                          String departmentAlias) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.lockerNumber = locker;
        this.boxNumber = box;
        this.departmentAlias = departmentAlias;
    }

    public SimpleEmployee(String firstName,
                          String lastName,
                          String comment,
                          String departmentAlias,
                          int lockerNumber,
                          int boxNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.comment = comment;
        this.departmentAlias = departmentAlias;
        this.lockerNumber = lockerNumber;
        this.boxNumber = boxNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getLockerNumber() {
        return lockerNumber;
    }

    public void setLockerNumber(int lockerNumber) {
        this.lockerNumber = lockerNumber;
    }

    public int getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(int boxNumber) {
        this.boxNumber = boxNumber;
    }

    public String getDepartmentAlias() {
        return departmentAlias;
    }

    public void setDepartmentAlias(String departmentAlias) {
        this.departmentAlias = departmentAlias;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
