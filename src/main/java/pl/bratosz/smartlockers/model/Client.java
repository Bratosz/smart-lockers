package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.*;

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

    @JsonView(Views.Public.class)
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private Set<ClientArticle> articles;

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

    public Department getDepartmentByName(String departmentName) {
        Optional<Department> departmentOpt = departments.stream().filter(d -> d.getName().equals(departmentName))
                .findFirst();
        return departmentOpt.orElseThrow(NoSuchElementException::new);
    }

    public Location getLocationByName(String locationName) {
        Optional<Location> locationOpt = locations.stream().filter(l -> l.getName().equals(locationName))
                .findFirst();
        return locationOpt.orElseThrow(NoSuchElementException::new);
    }

    public Plant getPlantByNumber(int plantNumber) {
        Optional<Plant> plantOpt = plants.stream().filter(p -> p.getPlantNumber() == plantNumber)
                .findFirst();
        return plantOpt.orElseThrow(NoSuchElementException::new);
    }

    public Set<ClientArticle> getArticles() {
        return articles;
    }

    public void setArticles(Set<ClientArticle> articles) {
        this.articles = articles;
    }

    public void addArticle(ClientArticle article) {
        if(this.articles == null)
            this.articles = new HashSet<>();
        this.articles.add(article);
    }
}
