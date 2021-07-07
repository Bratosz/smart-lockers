package pl.bratosz.smartlockers.model.clothes;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.Views;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(Views.Public.class)
    private int number;

    @JsonView(Views.Public.class)
    private String name;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    private ClothType clothType;

    @OneToMany(mappedBy = "clientArticle")
    private Set<Cloth> clothes;

    @OneToMany(mappedBy = "article")
    private Set<ClientArticle> clientArticles;

    public Article() {

    }

    public Article(int number, String name, ClothType clothType) {
        this.number = number;
        this.name = name;
        this.clothType = clothType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public Set<ClientArticle> getClientArticles() {
        return clientArticles;
    }

    public void setClientArticles(Set<ClientArticle> clientArticles) {
        this.clientArticles = clientArticles;
    }
}
