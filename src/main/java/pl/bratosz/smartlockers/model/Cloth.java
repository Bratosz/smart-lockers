package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonValue;
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
    protected ClothName name;

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
    protected int ordinalNo;

    @JsonView(Public.class)
    protected int articleNo;

    @JsonView(Public.class)
    protected boolean isActive;

    public Cloth(){}

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Cloth(long id, Date assignment, Date lastWashing, Date releaseDate, int ordinalNo, int articleNo, Size size) {
        this.id = id;
        this.assignment = assignment;
        this.lastWashing = lastWashing;
        this.releaseDate = releaseDate;
        this.ordinalNo = ordinalNo;
        this.articleNo = articleNo;
        this.size = size;
        name = ClothName.getByArticleNo(articleNo);
        isActive = true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ClothName getName() {
        return name;
    }

    public void setName(ClothName name) {
        this.name = name;
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

    public int getOrdinalNo() {
        return ordinalNo;
    }

    public void setOrdinalNo(int ordinalNo) {
        this.ordinalNo = ordinalNo;
    }

    public int getArticleNo() {
        return articleNo;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public void setArticleNo(int articleNo) {
        this.articleNo = articleNo;
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
