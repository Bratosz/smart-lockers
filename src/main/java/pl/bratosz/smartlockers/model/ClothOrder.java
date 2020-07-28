package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
    private Article article;

    @JsonView(Views.Public.class)
    @OneToMany
    private Set<EmployeeCloth> employeeClothes;

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

    public Set<EmployeeCloth> getEmployeeClothes() {
        return employeeClothes;
    }

    public void setEmployeeClothes(Set<EmployeeCloth> employeeClothes) {
        this.employeeClothes = employeeClothes;
    }
}
