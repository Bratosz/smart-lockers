package pl.bratosz.smartlockers.service.managers;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.*;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.service.ClothStatusService;

import java.util.Date;
import java.util.List;

import static pl.bratosz.smartlockers.model.clothes.ClothDestination.*;
import static pl.bratosz.smartlockers.model.orders.OrderType.*;

@Service
public class ClothesCreator {
    private ClothStatusService clothStatusService;
    private OrderManager orderManager;
    private Cloth cloth;
    private User user;

    public ClothesCreator(ClothStatusService clothStatusService, OrderManager orderManager) {
        this.clothStatusService = clothStatusService;
        this.orderManager = orderManager;
    }

    public Cloth createNew(Cloth prototype, User user) {
        this.user = user;
        this.cloth = prototype;
        setClothStatus(FOR_ASSIGN);
        setOrdinalNumber();
        return cloth;

    }

    public Cloth createNewInstead(int ordinalNumber,
                                  Article article,
                                  ClothSize size,
                                  Employee employee) {
        ClothStatus clothStatus = clothStatusService.create(FOR_ASSIGN, user);
        this.cloth = new Cloth();
        cloth.setStatus(clothStatus);
        cloth.setActive(false);
        cloth.setCreated(new Date());
        cloth.setOrdinalNumber(ordinalNumber);
        cloth.setArticle(article);
        cloth.setSize(size);
        cloth.setEmployee(employee);
        return cloth;
    }

    private void setClothStatus (ClothDestination destiny) {
        ClothStatus clothStatus = clothStatusService.create(destiny, user);
        cloth.setStatus(clothStatus);
    }

    public Cloth createExisting(Cloth existing, User user) {
        this.user = user;
        this.cloth = existing;
        ClothDestination destiny = determineClothDestination();
        setClothStatus(destiny);
        setClothOrder(destiny);
        return cloth;
    }

    private ClothDestination determineClothDestination() {
        if (cloth.getLastWashing().equals(null)) {
            return FOR_RELEASE;
        } else {
            return FOR_WASH;
        }
    }

    private void setClothOrder(ClothDestination destiny) {
        switch (destiny) {
            case FOR_RELEASE:
                orderManager.createForExistingCloth(RELEASE, cloth, user);
                break;
            case FOR_WASH:
                orderManager.createForExistingCloth(EMPTY, cloth, user);
                break;
        }
    }

    private void setOrdinalNumber() {
        int articleNumber = cloth.getArticle().getNumber();
        List<Cloth> clothes = cloth.getEmployee().getClothes();
        int ordinalNumber = resolveOrdinalNumber(clothes, articleNumber);
        cloth.setOrdinalNumber(ordinalNumber);
    }

    private int resolveOrdinalNumber(List<Cloth> clothes, int articleNumber) {
        int articlesAmount = (int) clothes.stream()
                .filter(c -> c.getArticle().getNumber() == articleNumber)
                .count();
        return articlesAmount + 1;
    }


}
