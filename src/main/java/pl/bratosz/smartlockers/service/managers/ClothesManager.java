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
    private ClothesCreator creator;
    private User user;
    private Date date;
    private Set<Cloth> clothes;
    private Cloth cloth;

    public ClothesManager(ClothStatusService clothStatusService,
                          ClothesCreator creator) {
        this.clothStatusService = clothStatusService;
        this.creator = creator;
    }

    public void loadUserAndDate(User user, Date date) {
        this.user = user;
        this.date = date;
    }

    public Cloth createNewInstead(int ordinalNumber,
                                  Article article,
                                  ClothSize size,
                                  Employee employee) {
        return creator.createNewInstead(ordinalNumber, article, size, employee);
    }

    public Cloth createNew(Article article, ClothSize size, Employee employee) {
        Cloth prototype = new Cloth(
                article,
                size,
                employee,
                new Date());
        return creator.createNew(prototype, user);
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
        return creator.createExisting(cloth, user);

    }

    public List<Cloth> set(ClothDestination destiny, List<Cloth> clothes) {
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
