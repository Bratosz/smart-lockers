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
    protected Date lastWashing;

    @JsonView(Public.class)
    protected int ordinalNo;

    @JsonView(Public.class)
    protected int articleNo;

    @JsonView(Public.class)
    protected Date releasedToEmployee;

    public Cloth(){}

    public Cloth(long id, Date lastWashing, int ordinalNo, int articleNo) {
        this.id = id;
        name = ClothName.getByArticleNo(articleNo);
        this.lastWashing = lastWashing;
        this.ordinalNo = ordinalNo;
        this.articleNo = articleNo;
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

    public Date getReleasedToEmployee() {
        return releasedToEmployee;
    }

    public void setReleasedToEmployee(Date releasedToEmployee) {
        this.releasedToEmployee = releasedToEmployee;
    }

    public ClothName getName() {
        return name;
    }

    public void setName(ClothName name) {
        this.name = name;
    }
}
