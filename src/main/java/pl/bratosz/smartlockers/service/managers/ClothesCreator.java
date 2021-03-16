package pl.bratosz.smartlockers.service.managers;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.*;
import pl.bratosz.smartlockers.model.orders.ClothOrder;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.service.ClothStatusService;

import java.util.Date;
import java.util.List;

import static pl.bratosz.smartlockers.model.clothes.ClothActualStatus.*;
import static pl.bratosz.smartlockers.model.clothes.ClothDestination.*;
import static pl.bratosz.smartlockers.model.orders.OrderType.*;

@Service
public class ClothesCreator {
    private ClothStatusService clothStatusService;
    private Cloth cloth;
    private User user;

    public ClothesCreator(ClothStatusService clothStatusService) {
        this.clothStatusService = clothStatusService;
    }

    public Cloth createNew(Cloth prototype) {
        this.cloth = prototype;
        ClothDestination destiny = FOR_ASSIGN;
        determineOrdinalNumber();
        setClothStatus(destiny);
        return cloth;
    }

    public Cloth createNewInstead(int ordinalNumber,
                                  Article article,
                                  ClothSize size,
                                  Employee employee) {
        this.cloth = new Cloth();
        ClothDestination destiny = FOR_ASSIGN;
        setClothStatus(destiny);
        cloth.setActive(false);
        cloth.setCreated(new Date());
        cloth.setOrdinalNumber(ordinalNumber);
        cloth.setArticle(article);
        cloth.setSize(size);
        cloth.setEmployee(employee);
        return cloth;
    }

    public Cloth createExisting(Cloth existing, User user) {
        this.cloth = existing;
        ClothDestination destiny = determineClothDestination();
        setClothStatus(destiny, user);
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
        OrderManager orderManager = new OrderManager(user, date);
        ClothOrder clothOrder;
        switch (destiny) {
            case FOR_RELEASE:
                clothOrder = orderManager.createForExistingCloth(RELEASE, cloth);
                cloth.setClothOrder(clothOrder);
                break;
            case FOR_WASH:
                clothOrder = orderManager.createForExistingCloth(EMPTY, cloth);
                cloth.setClothOrder(clothOrder);
                break;
        }
    }

    private void setClothStatus(ClothDestination destiny, User user) {
        ClothStatus status = clothStatusService.create(
                destiny,
                cloth,
                user);
        cloth.setStatus(status);
    }

    private void determineOrdinalNumber() {
        int articleNumber = cloth.getArticle().getArticleNumber();
        List<Cloth> clothes = cloth.getEmployee().getClothes();
        int ordinalNumber = resolveOrdinalNumber(clothes, articleNumber);
        cloth.setOrdinalNumber(ordinalNumber);
    }

    private int resolveOrdinalNumber(List<Cloth> clothes, int articleNumber) {
        int articlesAmount = (int) clothes.stream()
                .filter(c -> c.getArticle().getArticleNumber() == articleNumber)
                .count();
        return articlesAmount + 1;
    }


}
