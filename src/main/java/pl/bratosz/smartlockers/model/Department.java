package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    private Client client;

    @ManyToMany
    @JoinTable(
            name = "plants",
            joinColumns = @JoinColumn(name = "department_id"),
            inverseJoinColumns = @JoinColumn(name = "plant_id")
    )
    private Set<Plant> plants;

    @ManyToMany(mappedBy = "departments")
    private Set<Location> locations;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private Set<Employee> employees;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private Set<Locker> lockers;

    private int mainPlantNumber;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private Set<UserClient> userClients;

    public Department() {
    }

    public Department(String name, Client client, Set<Plant> plants, int mainPlantNumber) {
        this.name = name;
        this.client = client;
        this.plants = plants;
        this.mainPlantNumber = mainPlantNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Plant> getPlants() {
        return plants;
    }

    public void setPlants(Set<Plant> plants) {
        this.plants = plants;
    }

    public void addPlant(Plant plant) {
        plants.add(plant);
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    public Set<Locker> getLockers() {
        return lockers;
    }

    public void setLockers(Set<Locker> lockers) {
        this.lockers = lockers;
    }

    public int getMainPlantNumber() {
        return mainPlantNumber;
    }

    public void setMainPlantNumber(int mainPlantNumber) {
        this.mainPlantNumber = mainPlantNumber;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Set<UserClient> getUserClients() {
        return userClients;
    }

    public void setUserClients(Set<UserClient> userClients) {
        this.userClients = userClients;
    }
}
