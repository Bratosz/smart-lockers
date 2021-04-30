package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.clothes.Article;

import javax.persistence.*;

@Entity
public class ClientArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    @ManyToOne
    private Article article;

    @JsonView(Views.Public.class)
    private double redemptionPrice;

    @JsonView(Views.Public.class)
    private boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @JsonView(Views.Public.class)
    private int depreciationPercentageCap;

    @JsonView(Views.Public.class)
    private int depreciationPeriod;

    public ClientArticle() {
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
    public int getDepreciationPercentageCap() {
        return depreciationPercentageCap;
    }

    public void setDepreciationPercentageCap(int depreciationPercentageCap) {
        this.depreciationPercentageCap = depreciationPercentageCap;
    }

    public int getDepreciationPeriod() {
        return depreciationPeriod;
    }

    public void setDepreciationPeriod(int depreciationPeriod) {
        this.depreciationPeriod = depreciationPeriod;
    }

    public void addClient(Client client) {
        client.addArticle(this);
        setClient(client);
    }
}
