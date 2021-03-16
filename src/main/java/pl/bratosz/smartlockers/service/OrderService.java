package pl.bratosz.smartlockers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.orders.ActionType;
import pl.bratosz.smartlockers.model.orders.parameters.basic.BasicOrderParameters;
import pl.bratosz.smartlockers.model.orders.ClothOrder;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.model.orders.parameters.basic.ParametersForExchangeAndRelease;
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
    private User user;
    private Date date;

    public OrderService(OrdersRepository ordersRepository, UserService userService,
                        ArticleService articleService) {
        this.ordersRepository = ordersRepository;
        this.userService = userService;
        this.articleService = articleService;
    }

    public void loadUserAndManager(long userId) {
        user = userService.getUserById(userId);
        loadUserAndManager(user);
    }

    public void loadUserAndManager(User user) {
        this.user = user;
        date = new Date();
        orderManager = new OrderManager(user, date);
    }

    public List<ClothOrder> placeMany(
            OrderType orderType,
            int articleNumber,
            ClothSize size,
            long[] clothIds,
            long userId) {
        loadUserAndManager(userId);
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
        loadUserAndManager(user);
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
        ClothOrder order = orderManager.createOne(completeParameters);
        return ordersRepository.save(order);
    }

    public List<ClothOrder> performActionOnOrders(
            ActionType actionType,
            long[] clothOrdersIds,
            long userId) {
        List<ClothOrder> clothOrders = getClothOrders(clothOrdersIds);
        clothOrders = orderManager.perform(actionType, clothOrders);
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
