package pl.bratosz.smartlockers.model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Article {

    @Id
    private long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ClothType clothType;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private Set<Cloth> clothes;

    public Article() {

    }

    public Article(String name, ClothType clothType) {
        this.name = name;
        this.clothType = clothType;
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

    public ClothType getClothType() {
        return clothType;
    }

    public void setClothType(ClothType clothType) {
        this.clothType = clothType;
    }

    public Set<Cloth> getClothes() {
        return clothes;
    }

    public void setClothes(Set<Cloth> clothes) {
        this.clothes = clothes;
    }
}
