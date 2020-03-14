package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;


@Entity
public class Locker {
    @JsonView(Views.Public.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(Views.Public.class)
    private int lockerNumber;

    @JsonView(Views.Public.class)
    private Integer capacity;

    @JsonView(Views.Public.class)
    @Enumerated(EnumType.STRING)
    private Department department;

    @JsonView(Views.Public.class)
    @Enumerated(EnumType.STRING)
    private DepartmentNumber departmentNumber;

    @JsonView(Views.Public.class)
    @Enumerated(EnumType.STRING)
    private Location location;

    @JsonView(Views.InternalForLockers.class)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "locker_id")
    private List<Box> boxes;


    public Locker() {
    }

    public Locker(
            int lockerNumber,
            Integer capacity,
            Department department,
            DepartmentNumber departmentNumber,
            Location location) {
        this.lockerNumber = lockerNumber;
        this.capacity = capacity;
        this.department = department;
        this.departmentNumber = departmentNumber;
        this.location = location;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public DepartmentNumber getDepartmentNumber() {
        return departmentNumber;
    }

    public void setDepartmentNumber(DepartmentNumber departmentNumber) {
        this.departmentNumber = departmentNumber;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLockerNumber() {
        return lockerNumber;
    }

    public void setLockerNumber(int lockerNumber) {
        this.lockerNumber = lockerNumber;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Optional<Box> getBoxByNumber(Integer boxNumber) {
        return boxes.stream().filter(x -> x.getBoxNumber() == boxNumber).findFirst();
    }

    public enum DepartmentNumber {
        DEP_384(384),
        DEP_385(385),
        DEP_386(386),
        DEP_000(0);

        private int number;

        DepartmentNumber(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }
    }

    public enum Location {
        OLDSIDE("Stara część"),
        NEWSIDE("Nowa część"),
        MANTRANS("Mantrans"),
        NEWSIDEUPSTAIRS("Nowa część na piętrze"),
        NEWJITSIDE("Nowa część na JIT"),
        DISABLED("Nieaktywna");

        private String name;

        @JsonCreator
        public static Location forValue(String value) {
            if (value.equals("null")) {
                return null;
            } else {
                return Location.valueOf(value);
            }
        }

        Location(String name) {
            this.name = name;
        }
    }
}
