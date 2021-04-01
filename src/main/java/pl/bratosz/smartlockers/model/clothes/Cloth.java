package pl.bratosz.smartlockers.model.clothes;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.orders.ClothOrder;

import javax.persistence.*;
import java.util.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Cloth {
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    protected long id;

    @JsonView(Views.Public.class)
    @Enumerated(EnumType.STRING)
    protected ClothSize size;

    @JsonView(Views.Public.class)
    protected Date created;

    @JsonView(Views.Public.class)
    protected int ordinalNumber;

    @JsonView({Views.InternalForBoxes.class, Views.InternalForClothOrders.class})
    @ManyToOne
    protected Article article;

    @JsonView(Views.InternalForClothes.class)
    @OneToOne(mappedBy = "clothToExchange", cascade = CascadeType.ALL)
    private ClothOrder exchangeOrder;

    @JsonView(Views.InternalForClothes.class)
    @OneToOne(mappedBy = "clothToRelease", cascade = CascadeType.ALL)
    private ClothOrder releaseOrder;

    @JsonView(Views.InternalForClothes.class)
    @ManyToOne
    protected Employee employee;

    @JsonView({Views.InternalForEmployees.class, Views.InternalForBoxes.class})
    @OneToMany(mappedBy = "cloth")
    protected List<ClothStatus> statusHistory;

    @JsonView(Views.Public.class)
    long barCode;

    @JsonView(Views.Public.class)
    protected Date assignment;

    @JsonView(Views.Public.class)
    protected Date lastWashing;

    @JsonView(Views.Public.class)
    protected Date releaseDate;

    @JsonView(Views.Public.class)
    protected boolean active;

    public Cloth(){}

    public Cloth(
            int ordinalNumber,
            Article article,
            ClothSize size,
            Employee employee,
            Date created
    ) {
        this.ordinalNumber = ordinalNumber;
        this.article = article;
        this.size = size;
        this.employee = employee;
        this.created = created;
        active = false;
    }

    public Cloth(
            Article article,
            ClothSize size,
            Employee employee,
            Date created
    ) {
        this.size = size;
        this.created = created;
        this.article = article;
        this.employee = employee;
        active = false;
    }

    public Cloth(
        long barCode,
        Date assignment,
        Date lastWashing,
        Date releaseDate,
        int ordinalNumber,
        Article article,
        ClothSize size
    ) {
        this.barCode = barCode;
        this.assignment = assignment;
        setLastWashing(lastWashing);
        this.releaseDate = releaseDate;
        this.ordinalNumber = ordinalNumber;
        this.article = article;
        this.size = size;
        active = true;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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

    public ClothOrder getExchangeOrder() {
        return exchangeOrder;
    }

    public void setExchangeOrder(ClothOrder exchangeOrder) {
        exchangeOrder.setClothToRelease(this);
        this.exchangeOrder = exchangeOrder;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public List<ClothStatus> getStatusHistory() {
        return statusHistory;
    }

    public ClothStatus getClothStatus() {
        return statusHistory.get(statusHistory.size() -1);
    }

    public void setStatusHistory(List<ClothStatus> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public void setStatus(ClothStatus status) {
        status.setCloth(this);
        if(statusHistory == null) statusHistory = new LinkedList<>();
        statusHistory.add(status);
    }

    public long getBarCode() {
        return barCode;
    }

    public void setBarCode(long barCode) {
        this.barCode = barCode;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ClothOrder getReleaseOrder() {
        return releaseOrder;
    }

    public void setReleaseOrder(ClothOrder releaseOrder) {
        this.releaseOrder = releaseOrder;
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
}
