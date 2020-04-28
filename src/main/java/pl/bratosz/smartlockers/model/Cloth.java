package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Date;

import static pl.bratosz.smartlockers.model.Views.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Cloth {

    @JsonView(Public.class)
    @Id protected long id;

    @JsonView(Public.class)
    protected ClothName name;

    @JsonView(Public.class)
    protected Size size;

    @JsonView(Public.class)
    protected Date assignment;

    @JsonView(Public.class)
    protected Date lastWashing;

    @JsonView(Public.class)
    protected Date release;

    @JsonView(Public.class)
    protected int ordinalNo;

    @JsonView(Public.class)
    protected int articleNo;

    public Cloth(){}

    public Cloth(long id, Size size, Date assignment, Date lastWashing, Date release, int ordinalNo, int articleNo) {
        this.id = id;
        this.size = size;
        this.assignment = assignment;
        this.lastWashing = lastWashing;
        this.release = release;
        this.ordinalNo = ordinalNo;
        this.articleNo = articleNo;
        name = ClothName.getByArticleNo(articleNo);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getLastWashing() {
        return lastWashing;
    }

    public void setLastWashing(Date lastWashing) {
        this.lastWashing = lastWashing;
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

    public void setArticleNo(int articleNo) {
        this.articleNo = articleNo;
    }

    public ClothName getName() {
        return name;
    }

    public void setName(ClothName name) {
        this.name = name;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Date getAssignment() {
        return assignment;
    }

    public void setAssignment(Date assignment) {
        this.assignment = assignment;
    }

    public Date getRelease() {
        return release;
    }

    public void setRelease(Date release) {
        this.release = release;
    }
}
