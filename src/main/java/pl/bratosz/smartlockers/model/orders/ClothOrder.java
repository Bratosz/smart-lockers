package pl.bratosz.smartlockers.model.orders;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.ArticleType;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.Views;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
public class ClothOrder implements OrderForRelease, OrderForExchangeAndRelease {

    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(Views.Public.class)
    @OneToOne
    private Cloth clothToExchange;

    @JsonView(Views.Public.class)
    @OneToOne(cascade = CascadeType.ALL)
    private Cloth clothToRelease;

    @JsonView(Views.InternalForClothOrders.class)
    @ManyToOne
    private Employee employee;

    @JsonView(Views.Public.class)
    @OneToMany(mappedBy = "clothOrder", cascade = CascadeType.ALL)
    private List<OrderStatus> orderStatusHistory;

    @JsonView(Views.Public.class)
    private OrderType orderType;

    @JsonView(Views.Public.class)
    private String note;

    @JsonView(Views.Public.class)
    private boolean active;

    public ClothOrder() {
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public ArticleType getArticle() {
        return clothToRelease.getArticleType();
    }

    @Override
    public ClothSize getSize() {
        return clothToRelease.getSize();
    }

    public Cloth getClothToRelease() {
        return clothToRelease;
    }

    public void setClothToRelease(Cloth clothToRelease) {
        this.clothToRelease = clothToRelease;
    }

    public Cloth getClothToExchange() {
        return clothToExchange;
    }

    public void setClothToExchange(Cloth clothToExchange) {
        this.clothToExchange = clothToExchange;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<OrderStatus> getOrderStatusHistory() {
        return orderStatusHistory;
    }

    public void setOrderStatusHistory(List<OrderStatus> orderStatusHistory) {
        this.orderStatusHistory = orderStatusHistory;
    }

    public OrderStatus getOrderStatus() {
        int last = orderStatusHistory.size() - 1;
        return orderStatusHistory.get(last);
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        orderStatus.setClothOrder(this);
        if(orderStatusHistory == null) orderStatusHistory = new LinkedList<>();
        orderStatusHistory.add(orderStatus);
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}