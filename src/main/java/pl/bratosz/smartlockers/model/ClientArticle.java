package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.clothes.ArticleType;

import javax.persistence.*;

@Entity
public class ClientArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(Views.Public.class)
    @ManyToOne
    private ArticleType articleType;

    @JsonView(Views.Public.class)
    private double redemptionPrice;

    @JsonView(Views.Public.class)
    private boolean available;

    @JsonView(Views.Public.class)
    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    public ClientArticle() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArticleType getArticleType() {
        return articleType;
    }

    public void setArticleType(ArticleType articleType) {
        this.articleType = articleType;
    }

    public double getRedemptionPrice() {
        return redemptionPrice;
    }

    public void setRedemptionPrice(double redemptionPrice) {
        this.redemptionPrice = redemptionPrice;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
