package pl.bratosz.smartlockers.model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    private Plant plant;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private Set<Locker> lockers;

    public Location() {
    }

    public Location(String name, Plant plant, Set<Locker> lockers) {
        this.name = name;
        this.plant = plant;
        this.lockers = lockers;
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

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Set<Locker> getLockers() {
        return lockers;
    }

    public void setLockers(Set<Locker> lockers) {
        this.lockers = lockers;
    }
}
