package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.exception.UserPermissionException;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ClothOrder {

    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView({Views.InternalForClothOrders.class, Views.InternalForBoxes.class})
    @ManyToOne(cascade = CascadeType.ALL)
    private Article article;

    @JsonView(Views.Public.class)
    private ClothSize size;

    @JsonView(Views.Public.class)
    @ManyToOne
    private Cloth cloth;

    @JsonView(Views.Public.class)
    private OrderStatus orderStatus;

    private Date requestDate;

    @ManyToOne(cascade = CascadeType.ALL)
    private User requestedBy;

    @ManyToOne(cascade = CascadeType.ALL)
    private User confirmedBy;

    @ManyToOne(cascade = CascadeType.ALL)
    private User acceptedBy;

    @ManyToOne(cascade = CascadeType.ALL)
    private UserOurStaff inRealizationBy;

    @ManyToOne(cascade = CascadeType.ALL)
    private UserOurStaff preparedBy;

    @ManyToOne(cascade = CascadeType.ALL)
    private UserOurStaff defferedBy;

    @JsonView(Views.Public.class)
    private Date acceptDate;

    private Date realizationOpening;

    private Date estimatedDateOfExecution;

    private Date setAsDeffered;

    private Date defferedCancelDate;

    private Date finalizationDate;

    private Date confirmedDate;

    private boolean isDeffered;

    @JsonView(Views.Public.class)
    private boolean isActive;

    @JsonView(Views.Public.class)
    private boolean isFinalized;

    @JsonView(Views.Public.class)
    private boolean isCancelled;

    @JsonView(Views.Public.class)
    private boolean isAccepted;

    private boolean isReadyForRealization;

    @JsonView(Views.Public.class)
    private OrderType orderType;

    private String note;

    @JsonView(Views.InternalForClothOrders.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Employee employee;

    public ClothOrder() {
    }

    public ClothOrder(Employee employee, Cloth cloth, OrderType orderType, Article article,
                      ClothSize desiredSize, OrderStatus orderStatus, Date date, User user) {
        this.employee = employee;
        this.cloth = cloth;
        this.orderType = orderType;
        if(orderType.equals(OrderType.NEW_ARTICLE) || cloth.isAcceptedForExchange()){
            isReadyForRealization = true;
        } else {
            isReadyForRealization = false;
        }
        this.article = article;
        this.size = desiredSize;
        this.orderStatus = orderStatus;
        isActive = true;
        isFinalized = false;
        isDeffered = false;
        setDateAndUserResponsibleForOrderStatus(user, date, date);
    }

    private void setDateAndUserResponsibleForOrderStatus(
            User user, Date beginDate, Date endDate) {
        switch (orderStatus) {
            case REQUESTED_AND_PENDING_FOR_CONFIRMATION:
                setRequestedBy(user);
                setRequestDate(beginDate);
                break;
            case CONFIRMED_AND_PENDING_FOR_ACCEPTANCE:
                setConfirmedBy(user);
                setConfirmedDate(beginDate);
                break;
            case DECLINED_BY_CLIENT:
            case DEFFERED:
                setDeffered(true);
                setDefferedBy(user);
                setDefferedCancelDate(endDate);
                break;
            case ACCEPTED_AND_PENDING_FOR_REALIZATION:
                setAcceptedBy(user);
                setAcceptDate(beginDate);
                setAccepted(true);
                break;
            case IN_REALIZATION:
                setRealizationOpening(beginDate);
                setEstimatedDateOfRealization(endDate);
                break;
            case FINALIZED:
                setFinalizationDate(beginDate);
                setActive(false);
                setFinalized(true);
            case CANCELLED:
                setActive(false);
                setCancelled(true);
        }
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public ClothSize getSize() {
        return size;
    }

    public void setSize(ClothSize size) {
        this.size = size;
    }

    public Cloth getCloth() {
        return cloth;
    }

    public void setCloth(Cloth cloth) {
        this.cloth = cloth;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(
            OrderStatus orderStatus, User user, Date beginDate, Date endDate) {
        if(canStatusBeChanged(orderStatus)) {
            this.orderStatus = orderStatus;
            setDateAndUserResponsibleForOrderStatus(user, beginDate, endDate);
        }
    }

    private boolean canStatusBeChanged(OrderStatus orderStatus) {
        if(orderStatus.equals(OrderStatus.CANCELLED)){
            return true;
        }
        int x = this.orderStatus.getOrdinalNumber();
        int y = orderStatus.getOrdinalNumber();
        if(x == y || (x - y == -1)){
            return true;
        } else {
            return false;
        }
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public User getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(User confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public Date getConfirmedDate() {
        return confirmedDate;
    }

    public void setConfirmedDate(Date confirmedDate) {
        this.confirmedDate = confirmedDate;
    }

    public User getAcceptedBy() {
        return acceptedBy;
    }

    public void setAcceptedBy(User acceptedBy) {
        this.acceptedBy = acceptedBy;
    }

    public Date getAcceptDate() {
        return acceptDate;
    }

    public void setAcceptDate(Date acceptDate) {
        this.acceptDate = acceptDate;
    }

    public Date getEstimatedDateOfExecution() {
        return estimatedDateOfExecution;
    }

    public void setEstimatedDateOfRealization(Date estimatedDateOfExecution) {
        this.estimatedDateOfExecution = estimatedDateOfExecution;
    }

    public UserOurStaff getInRealizationBy() {
        return inRealizationBy;
    }

    public void setInRealizationBy(UserOurStaff inRealizationBy) {
        this.inRealizationBy = inRealizationBy;
    }

    public UserOurStaff getPreparedBy() {
        return preparedBy;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getRealizationOpening() {
        return realizationOpening;
    }

    public void setRealizationOpening(Date realizationOpening) {
        this.realizationOpening = realizationOpening;
    }

    public boolean isDeffered() {
        return isDeffered;
    }

    public void setDeffered(boolean deffered) {
        isDeffered = deffered;
    }

    public Date getDefferedCancelDate() {
        return defferedCancelDate;
    }

    public void setDefferedCancelDate(Date defferedCancelDate) {
        this.defferedCancelDate = defferedCancelDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public UserOurStaff getDefferedBy() {
        return defferedBy;
    }

    public void setDefferedBy(User user) {
        if (user.getClass().isInstance(UserOurStaff.class)) {
            this.defferedBy = (UserOurStaff) user;
        } else {
            String msg = "Użytkownik nie ma uprawnień";
            try {
                throw new UserPermissionException(msg);
            } catch (UserPermissionException e) {
                e.getMessage();
                e.printStackTrace();
            }
        }
    }

    public Date getSetAsDeffered() {
        return setAsDeffered;
    }

    public void setSetAsDeffered(Date setAsDeffered) {
        this.setAsDeffered = setAsDeffered;
    }

    public void setPreparedBy(UserOurStaff preparedBy) {
        this.preparedBy = preparedBy;
    }

    public Date getFinalizationDate() {
        return finalizationDate;
    }

    public void setFinalizationDate(Date finalizationDate) {
        this.finalizationDate = finalizationDate;
    }

    public void setEstimatedDateOfExecution(Date estimatedDateOfExecution) {
        this.estimatedDateOfExecution = estimatedDateOfExecution;
    }

    public void setDefferedBy(UserOurStaff defferedBy) {
        this.defferedBy = defferedBy;
    }

    public boolean isFinalized() {
        return isFinalized;
    }

    public void setFinalized(boolean finalized) {
        isFinalized = finalized;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public boolean isReadyForRealization() {
        return isReadyForRealization;
    }

    public void setReadyForRealization(boolean readyForRealization) {
        isReadyForRealization = readyForRealization;
    }

    @Override
    public String toString() {
        Cloth c = getCloth();
        Employee e = c.getEmployee();
        String clothOrderDescription = "Rodzaj zamówienia: " + getOrderType().getName() + "\n" +
                "Status zamówienia: " + getOrderStatus() + "\n" +
                "Ubranie zamówione: " + getArticle().getName() + " " + getSize() + "\n" +
                "Ubranie zdane: " + c.getArticle().getName() + " " + c.getSize().getName() + " lp. "
                + c.getOrdinalNumber() + "\n" +
                "Szafka: " + e.getBox().getLocker().getLockerNumber() + "/" + e.getBox().getBoxNumber() + "\n" +
                "Pracownik: " + e.getLastName() + " " + e.getFirstName();
        return clothOrderDescription;
    }
}
