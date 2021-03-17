package pl.bratosz.smartlockers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.orders.ActionType;
import pl.bratosz.smartlockers.model.orders.ClothOrder;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteForExchangeAndRelease;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteOrderParameters;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.OrdersRepository;
import pl.bratosz.smartlockers.service.managers.OrderManager;

import java.util.*;

@Service
public class OrderService {
    private OrdersRepository ordersRepository;
    private UserService userService;
    private ArticleService articleService;
    @Autowired
    private ClothService clothesService;
    private OrderManager orderManager;

    public OrderService(OrdersRepository ordersRepository,
                        UserService userService,
                        ArticleService articleService,
                        OrderManager orderManager) {
        this.ordersRepository = ordersRepository;
        this.userService = userService;
        this.articleService = articleService;
        this.orderManager = orderManager;
    }

    public List<ClothOrder> placeMany(
            OrderType orderType,
            int articleNumber,
            ClothSize size,
            long[] clothIds,
            long userId) {
        User user = userService.getUserById(userId);
        List<Cloth> clothesForExchange = clothesService.getClothesByIds(clothIds);
        Article article = articleService.get(articleNumber);
        List<ClothOrder> clothOrders = new LinkedList<>();
        clothesForExchange.stream().forEach(clothToExchange -> {
            ClothOrder order = placeOne(
                    clothToExchange,
                    orderType,
                    article,
                    size,
                    user);
            ((LinkedList<ClothOrder>) clothOrders).push(order);
        });
        return clothOrders;
    }

    public ClothOrder placeOne(Cloth clothForExchange,
                               OrderType orderType,
                               Article article,
                               ClothSize size,
                               User user) {
        Employee employee = clothForExchange.getEmployee();
        Cloth clothForRelease = clothesService.createNewInstead(
                clothForExchange.getOrdinalNumber(),
                article,
                size,
                employee,
                user);
        CompleteForExchangeAndRelease completeParameters =
                CompleteOrderParameters.createForClothExchangeAndRelease(
                clothForExchange,
                clothForRelease,
                orderType,
                user);
        ClothOrder order = orderManager.createOne(completeParameters, user);
        return ordersRepository.save(order);
    }

    public List<ClothOrder> performActionOnOrders(
            ActionType actionType,
            long[] clothOrdersIds,
            long userId) {
        User user = userService.getUserById(userId);
        List<ClothOrder> clothOrders = getClothOrders(clothOrdersIds);
        clothOrders = orderManager.perform(actionType, clothOrders, user);
        return ordersRepository.saveAll(clothOrders);
    }

    public Set<ClothOrder> getByEmployeeId(long employeeId) {
        return ordersRepository.getByEmployeeId(employeeId);
    }

    private List<ClothOrder> getClothOrders(long[] clothOrdersIds) {
        List<ClothOrder> clothOrders = new LinkedList<>();
        for (int i = 0; i < clothOrdersIds.length; i++) {
            long id = clothOrdersIds[i];
            ClothOrder clothOrder = ordersRepository.getById(id);
            clothOrders.add(clothOrder);
        }
        return clothOrders;
    }

}
