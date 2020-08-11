package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

import static pl.bratosz.smartlockers.model.Views.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Cloth {

    @JsonView(Public.class)
    @Id protected long id;

    @JsonView(Public.class)
    @Enumerated(EnumType.STRING)
    protected Size size;

    @JsonView(Public.class)
    protected Date assignment;

    @JsonView(Public.class)
    protected Date lastWashing;

    @JsonView(Public.class)
    protected Date releaseDate;

    @JsonView(Public.class)
    protected int ordinalNumber;

    @JsonView(Public.class)
    @ManyToOne(cascade = CascadeType.ALL)
    protected Article article;

    @JsonView(Public.class)
    protected boolean isActive;

    @OneToOne(cascade = CascadeType.ALL)
    private ClothOrder clothOrder;

    public Cloth(){}

    public Cloth(long id, Date assignment, Date lastWashing,
                 Date releaseDate, int ordinalNumber, Article article, Size size) {
        this.id = id;
        this.assignment = assignment;
        this.lastWashing = lastWashing;
        this.releaseDate = releaseDate;
        this.ordinalNumber = ordinalNumber;
        this.article = article;
        this.size = size;
        isActive = true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getAssignment() {
        return assignment;
    }

    public void setAssignment(Date assignment) {
        this.assignment = assignment;
    }

    public Date getLastWashing() {
        return lastWashing;
    }

    public void setLastWashing(Date lastWashing) {
        this.lastWashing = lastWashing;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getOrdinalNumber() {
        return ordinalNumber;
    }

    public void setOrdinalNumber(int ordinalNumber) {
        this.ordinalNumber = ordinalNumber;
    }

    public Article getArticle() {
        return article;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public void setArticleNo(Article article) {
        this.article = article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ClothOrder getClothOrder() {
        return clothOrder;
    }

    public void setClothOrder(ClothOrder clothOrder) {
        this.clothOrder = clothOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cloth cloth = (Cloth) o;
        return getId() == cloth.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
