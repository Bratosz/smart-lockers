package pl.bratosz.smartlockers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.ArticleType;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.orders.ActionType;
import pl.bratosz.smartlockers.model.orders.ClothOrder;
import pl.bratosz.smartlockers.model.orders.OrderStatus;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteForExchangeAndRelease;
import pl.bratosz.smartlockers.model.orders.parameters.complete.CompleteOrderParameters;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.OrdersRepository;
import pl.bratosz.smartlockers.response.ResponseOrdersCreated;
import pl.bratosz.smartlockers.service.managers.OrderManager;

import java.util.*;

import static pl.bratosz.smartlockers.model.orders.OrderStatus.OrderStage.READY_FOR_REALIZATION;

@Service
public class OrderService {
    private OrdersRepository ordersRepository;
    private UserService userService;
    private ArticleTypeService articleTypeService;
    @Autowired
    private ClothService clothesService;
    private OrderStatusService orderStatusService;
    private OrderManager orderManager;

    public OrderService(OrdersRepository ordersRepository,
                        UserService userService,
                        ArticleTypeService articleTypeService,
                        OrderStatusService orderStatusService, OrderManager orderManager) {
        this.ordersRepository = ordersRepository;
        this.userService = userService;
        this.articleTypeService = articleTypeService;
        this.orderStatusService = orderStatusService;
        this.orderManager = orderManager;
    }

    public ResponseOrdersCreated placeMany(
            OrderType orderType,
            int articleNumber,
            ClothSize size,
            long[] barcodes,
            long userId) {
        User user = userService.getUserById(userId);
        List<Cloth> clothes = clothesService.getByBarcodes(barcodes);
        List<Cloth> clothesWithActiveOrders = getClothesWithActiveOrders(clothes);
        if (clothesWithActiveOrders.isEmpty()) {
            switch (orderType) {
                case EXCHANGE_FOR_A_NEW_ONE:
                    return exchangeForNewOnes(clothes, user, orderType);
                case CHANGE_SIZE:
                    return exchangeForAnotherSize(size, clothes, user, orderType);
                case CHANGE_ARTICLE:
                    return exchangeForAnotherArticle(articleNumber, size, clothes, user, orderType);
                default:
                    throw new IllegalStateException("Unexpected value: " + orderType);
            }
        } else {
            return ResponseOrdersCreated.createForOrdersNotCreatedBecauseActiveOrdersPresent(
                    orderType, clothesWithActiveOrders);
        }
    }

    public ClothOrder setOrderReadyForRealization(ClothOrder order, User user) {
        order = orderManager.update(
                READY_FOR_REALIZATION, order, user);
        ordersRepository.save(order);
        return order;
    }

    private List<Cloth> getClothesWithActiveOrders(List<Cloth> clothes) {
        List<Cloth> clothesWithActiveOrders = new LinkedList<>();
        for (Cloth cloth : clothes) {
            if (containsActiveOrder(cloth)) {
                clothesWithActiveOrders.add(cloth);
            }
        }
        return clothesWithActiveOrders;
    }

    private boolean containsActiveOrder(Cloth cloth) {
        if (cloth.getExchangeOrder() != null) {
            if (cloth.getExchangeOrder().isActive()) {
                return true;
            }
        } else if (cloth.getReleaseOrder() != null) {
            if (cloth.getReleaseOrder().isActive()) {
                return true;
            }
        }
        return false;
    }

    private ResponseOrdersCreated exchangeForAnotherArticle(int articleNumber,
                                                            ClothSize size,
                                                            List<Cloth> clothesForExchange,
                                                            User user,
                                                            OrderType orderType) {
        ArticleType articleType = articleTypeService.get(articleNumber);
        for (Cloth cloth : clothesForExchange) {
            OrderStatus orderStatus = orderStatusService.create(orderType, user);
            placeOne(cloth, orderType, orderStatus, articleType, size, user);
        }
        return ResponseOrdersCreated.createForOrdersCreated(orderType, clothesForExchange.size());
    }

    private ResponseOrdersCreated exchangeForAnotherSize(
            ClothSize size,
            List<Cloth> clothesForExchange,
            User user,
            OrderType orderType) {
        for (Cloth cloth : clothesForExchange) {
            OrderStatus orderStatus = orderStatusService.create(orderType, user);
            placeOne(cloth, orderType, orderStatus, cloth.getArticleType(), size, user);
        }
        return ResponseOrdersCreated.createForOrdersCreated(orderType, clothesForExchange.size());

    }

    private ResponseOrdersCreated exchangeForNewOnes(List<Cloth> clothesForExchange,
                                                     User user,
                                                     OrderType orderType) {
        for (Cloth cloth : clothesForExchange) {
            OrderStatus orderStatus = orderStatusService.create(orderType, user);
            placeOne(cloth, orderType, orderStatus, cloth.getArticleType(), cloth.getSize(), user);
        }
        return ResponseOrdersCreated.createForOrdersCreated(orderType, clothesForExchange.size());
    }


    //clear cloth and hard hardDelete previous cloth order
    public ClothOrder placeOne(Cloth clothForExchange,
                               OrderType orderType,
                               OrderStatus orderStatus,
                               ArticleType articleType,
                               ClothSize size,
                               User user) {
        deleteInactiveOrder(clothForExchange);
        Employee employee = clothForExchange.getEmployee();
        Cloth clothForRelease = clothesService.createNewForAssignInsteadExisting(
                clothForExchange.getOrdinalNumber(),
                articleType,
                size,
                employee,
                user);
        CompleteForExchangeAndRelease completeParameters =
                CompleteOrderParameters.createForClothExchangeAndRelease(
                        clothForExchange,
                        clothForRelease,
                        employee,
                        orderType,
                        orderStatus,
                        user);
        ClothOrder order = orderManager.createOne(completeParameters, user);
        return ordersRepository.save(order);
    }

    private void deleteInactiveOrder(Cloth clothForExchange) {
        if (clothForExchange.getExchangeOrder() != null) {
            ClothOrder exchangeOrder = clothForExchange.getExchangeOrder();
            hardDelete(exchangeOrder);
        }
    }

    public void hardDelete(long orderId) {
        ClothOrder order = ordersRepository.getById(orderId);
        hardDelete(order);
    }

    public void hardDelete(ClothOrder order) {
        hardDeleteOrderStatuses(order);
        ordersRepository.deleteById(order.getId());
        hardDeleteClothToRelease(order);
    }

    public List<ClothOrder> performActionOnOrders(
            ActionType actionType,
            long[] clothOrdersIds,
            long userId) {
        User user = userService.getUserById(userId);
        List<ClothOrder> clothOrders = getClothOrdersByIds(clothOrdersIds);
        clothOrders = orderManager.perform(actionType, clothOrders, user);
        return ordersRepository.saveAll(clothOrders);
    }

    public Set<ClothOrder> getByEmployeeId(long employeeId) {
        return ordersRepository.getByEmployeeId(employeeId);
    }

    private List<ClothOrder> getClothOrdersByIds(long[] clothOrdersIds) {
        List<ClothOrder> clothOrders = new LinkedList<>();
        for (int i = 0; i < clothOrdersIds.length; i++) {
            long id = clothOrdersIds[i];
            ClothOrder clothOrder = ordersRepository.getById(id);
            clothOrders.add(clothOrder);
        }
        return clothOrders;
    }

    private void hardDeleteClothToRelease(ClothOrder order) {
        Cloth cloth = order.getClothToRelease();
        clothesService.hardDelete(cloth);
    }

    private void hardDeleteOrderStatuses(ClothOrder order) {
        List<OrderStatus> orderStatusHistory = order.getOrderStatusHistory();
        orderStatusService.hardDelete(orderStatusHistory);
    }

    public ClothOrder getEmpty() {
        return new ClothOrder();
    }
}
