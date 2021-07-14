package pl.bratosz.smartlockers.model.clothes;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.orders.ClothOrder;

import javax.persistence.*;
import java.math.BigDecimal;
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

    @JsonView({Views.EmployeeCompleteInfo.class, Views.InternalForBoxes.class, Views.InternalForClothOrders.class})
    @ManyToOne
    protected ClientArticle clientArticle;

    @JsonView(Views.InternalForClothes.class)
    @OneToOne(mappedBy = "clothToExchange", cascade = CascadeType.PERSIST)
    private ClothOrder exchangeOrder;

    @JsonView(Views.InternalForClothes.class)
    @OneToOne(mappedBy = "clothToRelease", cascade = CascadeType.PERSIST)
    private ClothOrder releaseOrder;

    @JsonView(Views.InternalForClothes.class)
    @ManyToOne(fetch = FetchType.LAZY)
    protected Employee employee;

    @JsonView({Views.InternalForEmployees.class, Views.InternalForBoxes.class})
    @OneToMany(mappedBy = "cloth")
    protected List<ClothStatus> statusHistory;

    @JsonView(Views.Public.class)
    protected LifeCycleStatus lifeCycleStatus;

    @JsonView(Views.Public.class)
    long barcode;

    @JsonView(Views.Public.class)
    protected Date assignment;

    @JsonView(Views.Public.class)
    protected Date lastWashing;

    @JsonView(Views.Public.class)
    protected Date releaseDate;

    @JsonView(Views.Public.class)
    protected boolean active;

    @JsonView(Views.Public.class)
    protected Double actualRedemptionPrice;

    @JsonView(Views.Public.class)
    protected LengthModification lengthModification;

    public Cloth() {
    }

    public Cloth(
            int ordinalNumber,
            ClientArticle clientArticle,
            ClothSize size,
            Employee employee,
            Date created
    ) {
        this.ordinalNumber = ordinalNumber;
        this.clientArticle = clientArticle;
        this.size = size;
        this.employee = employee;
        this.created = created;
        active = false;
    }

    public Cloth(
            ClientArticle clientArticle,
            ClothSize size,
            Employee employee,
            Date created
    ) {
        this.size = size;
        this.created = created;
        this.clientArticle = clientArticle;
        this.employee = employee;
        active = false;
    }

    public Cloth(
            long barcode,
            Date assignment,
            Date lastWashing,
            Date releaseDate,
            int ordinalNumber,
            ClientArticle clientArticle,
            ClothSize size
    ) {
        this.barcode = barcode;
        this.assignment = assignment;
        setLastWashing(lastWashing);
        this.releaseDate = releaseDate;
        this.ordinalNumber = ordinalNumber;
        this.clientArticle = clientArticle;
        this.size = size;
        active = true;
    }

    public Cloth(long barcode,
                 Date assignment,
                 Date lastWashing,
                 Date releaseDate,
                 int ordinalNumber,
                 ClientArticle clientArticle,
                 ClothSize size,
                 LengthModification lengthModification,
                 LifeCycleStatus lifeCycleStatus) {
        this.barcode = barcode;
        this.assignment = assignment;
        setLastWashing(lastWashing);
        this.releaseDate = releaseDate;
        this.ordinalNumber = ordinalNumber;
        this.clientArticle = clientArticle;
        this.size = size;
        this.lengthModification = lengthModification;
        active = true;
        this.lifeCycleStatus = lifeCycleStatus;
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

    public ClientArticle getClientArticle() {
        return clientArticle;
    }

    public void setClientArticle(ClientArticle clientArticle) {
        this.clientArticle = clientArticle;
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
        return statusHistory.get(statusHistory.size() - 1);
    }

    public void setStatusHistory(List<ClothStatus> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public void setStatus(ClothStatus status) {
        status.setCloth(this);
        if (statusHistory == null) statusHistory = new LinkedList<>();
        statusHistory.add(status);
    }

    public Double getActualRedemptionPrice() {
        return actualRedemptionPrice;
    }

    public void setActualRedemptionPrice(Double actualRedemptionPrice) {
        this.actualRedemptionPrice = actualRedemptionPrice;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
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

    public LengthModification getLengthModification() {
        return lengthModification;
    }

    public void setLengthModification(LengthModification lengthModification) {
        this.lengthModification = lengthModification;
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
        return getClientArticle().getArticle().getName() + " " + getSize() + " " + "lp. " +
                getOrdinalNumber();
    }

    public long getClientId() {
        return getEmployee().getDepartment().getClient().getId();
    }

    public LifeCycleStatus getLifeCycleStatus() {
        return lifeCycleStatus;
    }

    public void setLifeCycleStatus(LifeCycleStatus lifeCycleStatus) {
        this.lifeCycleStatus = lifeCycleStatus;
    }
}
