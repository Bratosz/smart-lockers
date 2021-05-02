package pl.bratosz.smartlockers.model;

import javax.persistence.Embeddable;

@Embeddable
public class SimpleEmployee {
    private String firstName;
    private String lastName;
    private int locker;
    private int box;

    public SimpleEmployee(String firstName,
                          String lastName,
                          int locker,
                          int box) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.locker = locker;
        this.box = box;
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

    public int getLocker() {
        return locker;
    }

    public void setLocker(int locker) {
        this.locker = locker;
    }

    public int getBox() {
        return box;
    }

    public void setBox(int box) {
        this.box = box;
    }
}
