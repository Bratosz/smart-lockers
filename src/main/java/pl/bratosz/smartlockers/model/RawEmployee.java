package pl.bratosz.smartlockers.model;

public class RawEmployee {
    private String firstName;
    private String lastName;
    private int lockerNumber;
    private int boxNumber;
    private Locker.DepartmentNumber depNumber;
    private Department dep;
    private Locker.Location location;

    public RawEmployee() {
    }

    public RawEmployee(String firstName, String lastName, int lockerNumber, int boxNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.lockerNumber = lockerNumber;
        this.boxNumber = boxNumber;
    }

    public RawEmployee(String firstName, String lastName, int lockerNumber, int boxNumber,
                       Locker.DepartmentNumber depNumber, Department dep, Locker.Location location) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.lockerNumber = lockerNumber;
        this.boxNumber = boxNumber;
        this.depNumber = depNumber;
        this.dep = dep;
        this.location = location;
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

    public Locker.DepartmentNumber getDepNumber() {
        return depNumber;
    }

    public void setDepNumber(Locker.DepartmentNumber depNumber) {
        this.depNumber = depNumber;
    }

    public Department getDep() {
        return dep;
    }

    public void setDep(Department dep) {
        this.dep = dep;
    }

    public Locker.Location getLocation() {
        return location;
    }

    public void setLocation(Locker.Location location) {
        this.location = location;
    }
}
