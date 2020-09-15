package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(Views.Public.class)
    private int articleNumber;

    @JsonView(Views.Public.class)
    private String name;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    private ClothType clothType;

    @JsonView(Views.InternalForEmployees.class)
    @OneToMany(mappedBy = "article")
    private Set<Cloth> clothes;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private Set<ClothOrder> orders;

    public Article() {

    }

    public Article(int articleNumber, String name, ClothType clothType) {
        this.articleNumber = articleNumber;
        this.name = name;
        this.clothType = clothType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(int articleNumber) {
        this.articleNumber = articleNumber;
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

    public Set<ClothOrder> getOrders() {
        return orders;
    }

    public void setOrders(Set<ClothOrder> orders) {
        this.orders = orders;
    }
}
