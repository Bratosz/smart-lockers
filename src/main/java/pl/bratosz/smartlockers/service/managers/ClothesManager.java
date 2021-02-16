package pl.bratosz.smartlockers.service.managers;

import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.*;
import pl.bratosz.smartlockers.model.orders.ClothOrder;
import pl.bratosz.smartlockers.model.users.User;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static pl.bratosz.smartlockers.model.clothes.ClothActualStatus.*;
import static pl.bratosz.smartlockers.model.clothes.ClothDestination.*;

public class ClothesManager {
    private User user;
    private Date date;
    private Set<Cloth> clothes;
    private Cloth cloth;

    public ClothesManager(User user, Date date) {
        this.user = user;
        this.date = date;
    }

    public Cloth createNewInstead(int ordinalNumber, Article article, ClothSize size, Employee employee) {
        Cloth prototype = new Cloth(
                ordinalNumber,
                article,
                size,
                employee,
                date);
        ClothesCreator creator = new ClothesCreator(user, date);
        return creator.createNewInstead(prototype);
    }

    public Cloth createNew(Article article, ClothSize size, Employee employee) {
        Cloth prototype = new Cloth(
                article,
                size,
                employee,
                date
        );
        ClothesCreator creator = new ClothesCreator(user, date);
        return creator.createNew(prototype);
    }

    public Cloth createExisting(long barCode, Date assignment, Date lastWashing,
                                Date release, int ordinalNo, Article article,
                                ClothSize size) {
        cloth = new Cloth();
        cloth.setBarCode(barCode);
        cloth.setAssignment(assignment);
        cloth.setLastWashing(lastWashing);
        cloth.setReleaseDate(release);
        cloth.setOrdinalNumber(ordinalNo);
        cloth.setArticle(article);
        cloth.setSize(size);
        ClothesCreator creator = new ClothesCreator(user, date);
        return creator.createExisting(cloth);

    }

    public Set<Cloth> set(ClothDestination destiny, Set<Cloth> clothes) {
        Set<Cloth> updatedClothes = new HashSet<>();
        for(Cloth c : clothes) {
            c = updateCloth(destiny, c);
            updatedClothes.add(c);
        }
        return updatedClothes;
    }

    public Cloth updateCloth(ClothActualStatus actualStatus, Cloth clothToUpdate) {
        ClothDestination destination = null;
        switch(actualStatus) {
            case ORDERED:
                destination = FOR_ASSIGN;
                break;
            case ASSIGNED:
                destination = FOR_RELEASE;
                break;
            case IN_PREPARATION:
                destination = FOR_RELEASE;
                break;
            case RELEASED:
                destination = FOR_WASH;
                break;
            case ACCEPTED_FOR_EXCHANGE:
                destination = FOR_WITHDRAW_AND_EXCHANGE;
                break;
            case EXCHANGED:
            case WITHDRAWN:
                destination = FOR_DISPOSAL;
                break;
        }
        ClothStatus clothStatus = new ClothStatus(actualStatus, destination, user, date);
        clothToUpdate.setStatus(clothStatus);
        return clothToUpdate;
    }

    public Cloth updateCloth(ClothDestination destiny, Cloth c) {
        ClothActualStatus status = null;
        switch(destiny) {
            case FOR_ASSIGN:
                status = ORDERED;
                break;
            case FOR_RELEASE:
                status = ASSIGNED;
                break;
            case FOR_WASH:
                status = RELEASED;
                break;
            case FOR_WITHDRAW_AND_DELETE:
                status = RELEASED;
                break;
            case FOR_WITHDRAW_AND_EXCHANGE:
                status = RELEASED;
                break;
            case FOR_DISPOSAL:
                status = WITHDRAWN;
                break;
        }
        ClothStatus clothStatus = new ClothStatus(status, destiny, user, date);
        c.setStatus(clothStatus);
        return c;
    }

    private void updateClothesStatus(ClothDestination clothDestination) {
        for(Cloth c : clothes) {
            if(c.isActive()) {
                //czy zapisuje w clothesach
                updateCloth(clothDestination, c);
            }
        }
    }

    private void updateClothOrdersForWithdraw() {
        OrderManager orderManager = new OrderManager(user);
        ClothOrder order;
        for(Cloth c : clothes) {
            if(c.getClothOrder().isActive()) {
                order = c.getClothOrder();
                order = orderManager.cancel(order);
                //c.getClothOrder - czy zwraca zmienione zamówienie czy potrzebne jest ponowne przypisanie
                //zamówienia do ubrania?
                System.out.println("something");
                c.setClothOrder(order);
            }
        }
    }

    public Cloth updateOrder(Cloth cloth) {
        ClothOrder order = cloth.getClothOrder();
        ClothActualStatus actualStatus = cloth.getClothStatus().getStatus();
        OrderManager orderManager = new OrderManager(user);
        order = orderManager.update(order, actualStatus);
        cloth.setClothOrder(order);
        return cloth;
    }
}
