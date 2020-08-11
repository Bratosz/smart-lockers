package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;
import org.apache.xmlbeans.impl.xb.xsdschema.All;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    private String name;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private Set<Plant> plants;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private Set<Department> departments;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private Set<Location> locations;

    Client() {
    }

    public Client(String name) {
        this.name = name;
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

    public Set<Location> getLocations() {
        return locations;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }

    public Set<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<Department> departments) {
        this.departments = departments;
    }
}
