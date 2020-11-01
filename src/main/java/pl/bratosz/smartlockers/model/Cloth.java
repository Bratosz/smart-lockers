package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Cloth {
    @JsonView(Views.Public.class)
    @Id protected long id;

    @JsonView(Views.Public.class)
    @Enumerated(EnumType.STRING)
    protected ClothSize size;

    @JsonView(Views.Public.class)
    protected Date assignment;

    @JsonView(Views.Public.class)
    protected Date lastWashing;

    @JsonView(Views.Public.class)
    protected Date releaseDate;

    @JsonView(Views.Public.class)
    protected int ordinalNumber;

    @JsonView(Views.InternalForBoxes.class)
    @ManyToOne(cascade = CascadeType.ALL)
    protected Article article;

    @JsonView(Views.Public.class)
    protected boolean isActive;

    @JsonView(Views.InternalForClothes.class)
    @OneToMany(mappedBy = "cloth", cascade = CascadeType.ALL)
    private Set<ClothOrder> clothOrders;

    @JsonView(Views.InternalForClothes.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Employee employee;

    @JsonView(Views.InternalForClothes.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Employee acceptedOwner;

    @JsonView(Views.InternalForClothes.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Employee rotationOwner;

    @JsonView(Views.InternalForClothes.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Employee withdrawnOwner;

    @JsonView(Views.Public.class)
    private boolean acceptedForExchange;

    @JsonView(Views.Public.class)
    private Date acceptedForExchangeDate;

    private boolean rotational;

    private  boolean releaseAsRotational;

    private Date releaseAsRotationalDate;

    public Cloth(){}

    public Cloth(long id, Date assignment, Date lastWashing, Date release, int ordinalNo,
                         Article article, Employee employee, ClothSize size) {
        this.id = id;
        this.assignment = assignment;
        this.lastWashing = lastWashing;
        this.releaseDate = release;
        this.ordinalNumber = ordinalNo;
        this.article = article;
        this.size = size;
        this.employee = employee;
        acceptedForExchange = false;
        rotational = false;
        releaseAsRotational = false;
    }

    public Cloth(long id, ClothSize size, Date assignment, Date lastWashing,
                 Date releaseDate, int ordinalNumber, Article article, boolean isActive,
                 ClothOrder clothOrder, Employee employee, boolean acceptedForExchange, boolean rotational) {
        this.id = id;
        this.size = size;
        this.assignment = assignment;
        this.lastWashing = lastWashing;
        this.releaseDate = releaseDate;
        this.ordinalNumber = ordinalNumber;
        this.article = article;
        this.isActive = isActive;
        clothOrders.add(clothOrder);
        this.employee = employee;
        this.acceptedForExchange = acceptedForExchange;
        this.rotational = rotational;
    }

    public Cloth(long id, Date assignment, Date lastWashing, Date release,
                 int ordinalNo, Article article, ClothSize size) {
        this.id = id;
        this.assignment = assignment;
        this.lastWashing = lastWashing;
        this.releaseDate = release;
        this.ordinalNumber = ordinalNo;
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

    public ClothSize getSize() {
        return size;
    }

    public void setSize(ClothSize size) {
        this.size = size;
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

    public void setArticle(Article article) {
        this.article = article;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Set<ClothOrder> getClothOrders() {
        if(clothOrders.equals(null))
            return new HashSet<>();
        return clothOrders;
    }

    public void setClothOrders(Set<ClothOrder> clothOrders) {
        this.clothOrders = clothOrders;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public boolean isAcceptedForExchange() {
        return acceptedForExchange;
    }

    public void setAcceptedForExchange(boolean acceptedForExchange) {
        this.acceptedForExchange = acceptedForExchange;
    }

    public boolean isRotational() {
        return rotational;
    }

    public void setRotational(boolean rotational) {
        this.rotational = rotational;
    }

    public Employee getRotationOwner() {
        return rotationOwner;
    }

    public void setRotationOwner(Employee rotationOwner) {
        this.rotationOwner = rotationOwner;
    }

    public Date getReleaseAsRotationalDate() {
        return releaseAsRotationalDate;
    }

    public void setReleaseAsRotationalDate(Date releaseAsRotationalDate) {
        this.releaseAsRotationalDate = releaseAsRotationalDate;
    }

    public Employee getAcceptedOwner() {
        return acceptedOwner;
    }

    public void setAcceptedOwner(Employee acceptedOwner) {
        this.acceptedOwner = acceptedOwner;
    }

    public boolean isReleaseAsRotational() {
        return releaseAsRotational;
    }

    public void setReleaseAsRotational(boolean releaseAsRotational) {
        this.releaseAsRotational = releaseAsRotational;
    }

    public Date getAcceptedForExchangeDate() {
        return acceptedForExchangeDate;
    }

    public void setAcceptedForExchangeDate(Date acceptedForExchangeDate) {
        this.acceptedForExchangeDate = acceptedForExchangeDate;
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

    @Override
    public String toString() {
        return getArticle().getName() + " " + getSize() + " " + "lp. " +
                getOrdinalNumber();
    }

    public long getClientId() {
        return getEmployee().getDepartment().getClient().getId();
    }

    public Employee getWithdrawnOwner() {
        return withdrawnOwner;
    }

    public void setWithdrawnOwner(Employee withdrawnOwner) {
        this.withdrawnOwner = withdrawnOwner;
    }
}
