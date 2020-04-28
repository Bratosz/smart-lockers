package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class ExchangeStatus {

    @Id
    private long id;

    @JsonView(Views.Public.class)
    private ExchangeType exchangeType;

    @JsonView(Views.Public.class)
    private Size size;

    private int articleNo;

    @JsonView(Views.Public.class)
    private ClothName name;

    @OneToOne
    private EmployeeCloth employeeCloth;

    public ExchangeStatus(){}

    public ExchangeStatus(long id, ExchangeType exchangeType, Size size, int articleNo) {
        this.id = id;
        this.exchangeType = exchangeType;
        this.size = size;
        this.articleNo = articleNo;
        name = ClothName.getByArticleNo(articleNo);
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

    public int getArticleNo() {
        return articleNo;
    }

    public void setArticleNo(int articleNo) {
        this.articleNo = articleNo;
    }

    public ClothName getName() {
        return name;
    }

    public void setName(ClothName name) {
        this.name = name;
    }

    public EmployeeCloth getEmployeeCloth() {
        return employeeCloth;
    }

    public void setEmployeeCloth(EmployeeCloth employeeCloth) {
        this.employeeCloth = employeeCloth;
    }
}
