package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class ClothOrder {

    @Id
    private long id;

    @JsonView(Views.Public.class)
    private ExchangeType exchangeType;

    @JsonView(Views.Public.class)
    private Size size;

    @JsonView(Views.Public.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Article article;

    @JsonView(Views.Public.class)
    @OneToOne(mappedBy = "clothOrder", cascade = CascadeType.ALL)
    private Cloth cloth;

    @JsonView(Views.Public.class)
    private OrderStatus orderStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    private UserClient reportedBy;

    @JsonView(Views.Public.class)
    private Date reportDate;

    @JsonView(Views.Public.class)
    private Date estimatedDateOfExecution;

    @ManyToOne(cascade = CascadeType.ALL)
    private UserEmployee acceptedBy;

    private Date acceptDate;

    @ManyToOne(cascade = CascadeType.ALL)
    private UserEmployee realizedBy;

    @JsonView(Views.Public.class)
    private boolean isrealized;

    @JsonView(Views.Public.class)
    private Date realizeDate;



    public ClothOrder(){}

    public ClothOrder(long id, ExchangeType exchangeType, Size size, Article article) {
        this.id = id;
        this.exchangeType = exchangeType;
        this.size = size;
        this.article = article;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ExchangeType getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(ExchangeType exchangeType) {
        this.exchangeType = exchangeType;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Cloth getCloth() {
        return cloth;
    }

    public void setCloth(Cloth cloth) {
        this.cloth = cloth;
    }
}
