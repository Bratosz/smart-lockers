package pl.bratosz.smartlockers.service.managers;

import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothDestination;
import pl.bratosz.smartlockers.model.clothes.ClothStatus;
import pl.bratosz.smartlockers.model.orders.ClothOrder;
import pl.bratosz.smartlockers.model.users.User;

import java.util.Date;
import java.util.Set;

import static pl.bratosz.smartlockers.model.clothes.ClothActualStatus.*;
import static pl.bratosz.smartlockers.model.clothes.ClothDestination.*;
import static pl.bratosz.smartlockers.model.orders.OrderType.*;

public class ClothesCreator {
    private Cloth cloth;
    private User user;
    private Date date;

    public ClothesCreator(User user,
                          Date date) {
        this.user = user;
        this.date = date;
    }

    public Cloth createNew(Cloth prototype) {
        this.cloth = prototype;
        ClothDestination destiny = FOR_ASSIGN;
        setOrdinalNumber();
        setClothStatus(destiny);
        return cloth;
    }

    public Cloth createNewInstead(Cloth prototype) {
        this.cloth = prototype;
        ClothDestination destiny = FOR_ASSIGN;
        setClothStatus(destiny);
        return cloth;
    }

    public Cloth createExisting(Cloth existing) {
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
        OrderManager orderManager = new OrderManager(user, date);
        ClothOrder clothOrder;
        switch(destiny) {
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

    private void setClothStatus(ClothDestination destiny) {
        ClothStatus status;
        switch (destiny) {
            case FOR_ASSIGN:
                status = new ClothStatus(
                        ORDERED,
                        destiny,
                        user, date);
                cloth.setStatus(status);
                break;
            case FOR_RELEASE:
                status = new ClothStatus(
                        ASSIGNED,
                        destiny,
                        user, date);
                cloth.setStatus(status);
                break;
            case FOR_WASH:
                status = new ClothStatus(
                        RELEASED,
                        destiny,
                        user, date);
                cloth.setStatus(status);
                break;
        }
    }

    private void setOrdinalNumber() {
        int articleNumber = cloth.getArticle().getArticleNumber();
        Set<Cloth> clothes = cloth.getEmployee().getClothes();
        int ordinalNumber = resolveOrdinalNumber(clothes, articleNumber);
        cloth.setOrdinalNumber(ordinalNumber);
    }

    private int resolveOrdinalNumber(Set<Cloth> clothes, int articleNumber) {
        int articlesAmount = (int) clothes.stream()
                .filter(c -> c.getArticle().getArticleNumber() == articleNumber)
                .count();
        return articlesAmount + 1;
    }



}
