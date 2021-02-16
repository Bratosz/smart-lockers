package pl.bratosz.smartlockers.model.orders;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.model.users.User;

import javax.persistence.*;
import java.util.Date;

@Entity
public class OrderStatus {
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;

    @JsonView(Views.InternalForClothOrders.class)
    @ManyToOne
    private User user;

    @JsonView(Views.Public.class)
    private OrderStage actualStage;

    @JsonView(Views.Public.class)
    private Date dateOfUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    private ClothOrder clothOrder;

    public OrderStatus(){
    }

    public OrderStatus(OrderStage initialStage,
                       User user,
                       Date date) {
        this.actualStage = initialStage;
        this.dateOfUpdate = date;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrderStage getActualStage() {
        return actualStage;
    }

    public void setActualStage(OrderStage actualStage) {
        this.actualStage = actualStage;
    }

    public Date getDateOfUpdate() {
        return dateOfUpdate;
    }

    public void setDateOfUpdate(Date dateOfUpdate) {
        this.dateOfUpdate = dateOfUpdate;
    }

    public ClothOrder getClothOrder() {
        return clothOrder;
    }

    public void setClothOrder(ClothOrder clothOrder) {
        this.clothOrder = clothOrder;
    }

    public enum OrderStage {
        REQUESTED_AND_PENDING_FOR_CONFIRMATION_BY_SUPERVISOR("oczekuje na potwierdzenie przez klienta", false),
        CONFIRMED_BY_CLIENT_AND_PENDING_FOR_ACCEPTANCE("oczekuje na akceptację", true),
        DECLINED_BY_CLIENT("odrzucone przez kilenta",false),
        PENDING_FOR_ASSIGNMENT("oczekuje na przypisanie", true),
        ASSIGNED_AND_WAITING_FOR_RETURN("oczekuje na zwrot odzieży", true),
        //Ready means that clothForExchange for exchange is refunded and order should be realized,
        READY_FOR_REALIZATION("gotowe do wykonania", true),
        DEFFERED("odroczone", true),
        IN_REALIZATION("w realizacji",true),
        PREPARED("przygotowane", true),
        PACKED("spakowane", true),
        FINALIZED("sfinalizowane", false),
        CANCELLED("usunięte", false),
        EMPTY("brak zamówienia", false);

        private final String name;
        private final boolean active;

        OrderStage(String name, boolean active) {
            this.name = name;
            this.active = active;
        }

        @JsonValue
        public String getName() {
            return name;
        }

        public boolean isActive() {return active;}
    }
}
