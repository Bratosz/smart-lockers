package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.ArticleNotExistException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.OrdersRepository;

import java.util.*;
import java.util.function.Consumer;

@Service
public class OrderService {
    private OrdersRepository ordersRepository;
    private UserService userService;
    private ClothService clothesService;
    private ArticleService articleService;


    public OrderService(OrdersRepository ordersRepository, UserService userService,
                        ClothService clothesService, ArticleService articleService) {
        this.ordersRepository = ordersRepository;
        this.userService = userService;
        this.clothesService = clothesService;
        this.articleService = articleService;
    }

    public List<ClothOrder> place(int articleNumber, ClothSize size, OrderType orderType, long userId, long[] clothIds) {
        User user = userService.getOurUserById(userId);
        Permissions permissions = user.getPermissions();
        OrderStatus orderStatus = resolveInitialOrderStatusByUserPermissions(permissions);
        List<Cloth> clothes = clothesService.getClothesByIds(clothIds);
        try {
            return createOrders(orderType, articleNumber, size, orderStatus, clothes, user);
        } catch (ArticleNotExistException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<ClothOrder> createOrders(
            OrderType orderType, int articleNumber, ClothSize size,
            OrderStatus orderStatus, List<Cloth> clothes, User user) throws ArticleNotExistException {
        List<ClothOrder> clothOrders = new LinkedList<>();
        Date date = new Date();
        for (Cloth cloth : clothes) {
            ClothOrder order = create(cloth, orderType, articleNumber, size, orderStatus, date, user);
            clothOrders.add(order);
        }
        return clothOrders;
    }

    private ClothOrder create(
            Cloth cloth, OrderType orderType, int articleNumber,
            ClothSize size, OrderStatus orderStatus, Date date, User user) throws ArticleNotExistException {
        Employee employee = cloth.getEmployee();
        Article article = articleService.determineDesiredArticle(articleNumber, cloth.getArticle());
        ClothSize desiredSize = clothesService.determineDesiredSize(size, cloth.getSize());
        ClothOrder order = new ClothOrder(employee, cloth, orderType, article, desiredSize, orderStatus, date, user);
        return ordersRepository.save(order);
    }

    public Set<ClothOrder> performActionOnOrders(
            ActionType actionType, long userId, long[] clothOrdersIds) {
        Set<ClothOrder> clothOrders = getClothOrders(clothOrdersIds);
        User user = userService.getUserById(userId);
        Date actualDate = new Date();
        switch (actionType) {
            case ACCEPT:
                return acceptOrders(clothOrders, user, actualDate);
            case CANCEL:
                return cancelOrders(clothOrders, user, actualDate);
            default:
                return clothOrders;
        }
    }

    private OrderStatus resolveInitialOrderStatusByUserPermissions(Permissions permissions) {
        switch (permissions) {
            case CLIENT_BASIC:
                return OrderStatus.REQUESTED_AND_PENDING_FOR_CONFIRMATION;
            case CLIENT_MEDIUM:
                return OrderStatus.CONFIRMED_AND_PENDING_FOR_ACCEPTANCE;
            case CLIENT_FULL:
                return OrderStatus.CONFIRMED_AND_PENDING_FOR_ACCEPTANCE;
            default:
                return OrderStatus.CONFIRMED_AND_PENDING_FOR_ACCEPTANCE;
        }
    }

    public Set<ClothOrder> getByEmployeeId(long employeeId) {
        return ordersRepository.getByEmployeeId(employeeId);
    }

    private Set<ClothOrder> cancelOrders(
            Set<ClothOrder> clothOrders, User user, Date actualDate) {
        Consumer<ClothOrder> cancel = c -> {
            c.setOrderStatus(OrderStatus.CANCELLED, user, actualDate, actualDate);
            ordersRepository.save(c);
        };
        clothOrders.forEach(cancel);
        return clothOrders;
    }

    private Set<ClothOrder> acceptOrders(
            Set<ClothOrder> clothOrders, User user, Date actualDate) {
        Consumer<ClothOrder> accept = c -> {
            c.setOrderStatus(OrderStatus.ACCEPTED_AND_PENDING_FOR_REALIZATION,
                    user, actualDate, actualDate);
            ordersRepository.save(c);
        };
        clothOrders.forEach(accept);
        return clothOrders;
    }

    private Set<ClothOrder> getClothOrders(long[] clothOrdersIds) {
        Set<ClothOrder> clothOrders = new HashSet<>();
        for (int i = 0; i < clothOrdersIds.length; i++) {
            long id = clothOrdersIds[i];
            ClothOrder clothOrder = ordersRepository.getById(id);
            clothOrders.add(clothOrder);
        }
        return clothOrders;
    }
}
