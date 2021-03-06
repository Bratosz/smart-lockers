package pl.bratosz.smartlockers.service.managers;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.*;
import pl.bratosz.smartlockers.model.orders.ClothOrder;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.service.ClothStatusService;

import java.util.*;

@Service
public class ClothesManager {
    private ClothStatusService clothStatusService;
    private OrderManager orderManager;
    private ClothesCreator creator;
    private Set<Cloth> clothes;
    private Cloth cloth;

    public ClothesManager(ClothStatusService clothStatusService,
                          ClothesCreator creator,
                          OrderManager orderManager) {
        this.clothStatusService = clothStatusService;
        this.creator = creator;
        this.orderManager = orderManager;
    }

    public Cloth createNewInstead(int ordinalNumber,
                                  ClientArticle clientArticle,
                                  ClothSize size,
                                  Employee employee) {
        return creator.createNewInstead(ordinalNumber, clientArticle, size, employee);
    }

    public Cloth createNew(ClientArticle clientArticle, ClothSize size, Employee employee, User user) {
        Cloth prototype = new Cloth(
                clientArticle,
                size,
                employee,
                new Date());
        return creator.createNew(prototype, user);
    }

    public Cloth createExisting(Cloth cloth, User user) {
        return creator.createExisting(cloth, user);
    }

    public Cloth createExisting(long barCode,
                                Date assignment,
                                Date lastWashing,
                                Date release,
                                int ordinalNo,
                                ClientArticle clientArticle,
                                ClothSize size,
                                User user) {
        cloth = new Cloth();
        cloth.setBarcode(barCode);
        cloth.setAssignment(assignment);
        cloth.setLastWashing(lastWashing);
        cloth.setReleaseDate(release);
        cloth.setOrdinalNumber(ordinalNo);
        cloth.setClientArticle(clientArticle);
        cloth.setSize(size);
        return createExisting(cloth, user);
    }

    public List<Cloth> set(ClothDestination destiny,
                           List<Cloth> clothes,
                           User user) {
        List<Cloth> updatedClothes = new LinkedList<>();
        for(Cloth cloth : clothes) {
            ClothStatus clothStatus = clothStatusService.create(destiny, cloth, user);
            cloth = updateCloth(clothStatus, cloth);
            updatedClothes.add(cloth);
        }
        return updatedClothes;
    }

    public Cloth updateCloth(ClothStatus actualStatus, Cloth clothToUpdate) {
        clothToUpdate.setStatus(actualStatus);
        return clothToUpdate;
    }

    private void updateClothOrdersForWithdraw(User user) {
        ClothOrder order;
        for(Cloth c : clothes) {
            if(c.getExchangeOrder().isActive()) {
                order = c.getExchangeOrder();
                order = orderManager.cancel(order, user);
                //c.getExchangeOrder - czy zwraca zmienione zam??wienie czy potrzebne jest ponowne przypisanie
                //zam??wienia do ubrania?
                System.out.println("something");
                c.setExchangeOrder(order);
            }
        }
    }

    public Cloth updateOrder(Cloth cloth, User user) {
        ClothOrder order = cloth.getExchangeOrder();
        ClothActualStatus actualStatus = cloth.getClothStatus().getActualStatus();
        order = orderManager.update(order, actualStatus, user);
        cloth.setExchangeOrder(order);
        return cloth;
    }
}
