package pl.bratosz.smartlockers.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
public class Article {

    @Id
    private long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ClothType clothType;

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
}
