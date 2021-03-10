package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.users.UserClient;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    private String name;

    @JsonView(Views.Public.class)
    @OneToMany(mappedBy = "department")
    private List<DepartmentAlias> aliases;

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

    private boolean departmentDefault;

    public Department() {
    }

    public Department(String name,
                      Client client,
                      Set<Plant> plants,
                      int mainPlantNumber,
                      List<DepartmentAlias> aliases
    ) {
        this.name = name;
        this.client = client;
        this.plants = plants;
        this.mainPlantNumber = mainPlantNumber;
        addAliases(aliases);
    }

    public void addAliases(List<DepartmentAlias> aliases) {
        if (this.aliases == null) {
          aliases.stream()
                  .forEach(alias -> alias.setDepartment(this));
          setAliases(aliases);
        } else {
            aliases.stream()
                    .forEach(alias -> addAlias(alias));
        }
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

    public List<DepartmentAlias> getAliases() {
        return aliases;
    }

    public void setAliases(List<DepartmentAlias> aliases) {
        this.aliases = aliases;
    }

    public boolean isDepartmentDefault() {
        return departmentDefault;
    }

    public void setDepartmentDefault(boolean departmentDefault) {
        this.departmentDefault = departmentDefault;
    }

    public void addAlias(DepartmentAlias departmentAlias) {
        departmentAlias.setDepartment(this);
        getAliases().add(departmentAlias);
    }
}
