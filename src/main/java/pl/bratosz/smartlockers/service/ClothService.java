package pl.bratosz.smartlockers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.ClothOrderException;
import pl.bratosz.smartlockers.exception.NoActiveClothOrderException;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.orders.*;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.OrdersRepository;
import pl.bratosz.smartlockers.response.ResponseClothAcceptance;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.ClothesRepository;

import pl.bratosz.smartlockers.service.managers.OrderManager;
import pl.bratosz.smartlockers.service.managers.ClothesManager;

import java.util.*;

import static pl.bratosz.smartlockers.model.clothes.ClothActualStatus.ACCEPTED_FOR_EXCHANGE;
import static pl.bratosz.smartlockers.model.clothes.ClothSize.*;
import static pl.bratosz.smartlockers.model.orders.OrderType.*;

@Service
public class ClothService {
    private ClothesRepository clothesRepository;
    private OrdersRepository ordersRepository;
    @Autowired
    private OrderService orderService;
    private UserService userService;
    private User user;
    private Date date;
    private ClothesManager clothesManager;

    public ClothService(ClothesRepository clothesRepository,
                        OrdersRepository ordersRepository, UserService userService) {
        this.clothesRepository = clothesRepository;
        this.ordersRepository = ordersRepository;
        this.userService = userService;
    }

    public void loadManagerAndUser(long userId) {
        user = userService.getUserById(userId);
        date = new Date();
        clothesManager = new ClothesManager(user, date);
    }

    public void loadManagerAndUser(User user) {
        this.user = user;
        this.date = new Date();
        clothesManager = new ClothesManager(this.user, date);
    }

    public Cloth createNewInstead(
            int ordinalNumber,
            Article article,
            ClothSize size,
            Employee employee,
            User user
    ) {
        loadManagerAndUser(user);
        return clothesManager.createNewInstead(ordinalNumber, article, size, employee);
    }

    public List<Cloth> getClothesByIds(long[] clothsIds) {
        List<Cloth> clothes = new LinkedList<>();
        for (int i = 0; i < clothsIds.length; i++) {
            long clothId = clothsIds[i];
            Cloth cloth = clothesRepository.getClothById(clothId);
            clothes.add(cloth);
        }
        return clothes;
    }

    public Cloth getClothById(long id) {
        return clothesRepository.getClothById(id);
    }

    public ClothSize resolveDesiredSize(ClothSize desiredSize, ClothSize actualSize) {
        if (desiredSize == SIZE_SAME) {
            return actualSize;
        } else {
            return desiredSize;
        }
    }

    public ResponseClothAcceptance accept(long clientId, long userId, long clothId, OrderType orderType) {
        Cloth cloth = clothesRepository.getClothById(clothId);
        if (clothIsNotPresent(clientId, cloth)) {
            return createClothNotFoundResponse(cloth, clothId);
        } else {
            loadManagerAndUser(userId);
            OrderType actualOrderType = determineOrderType(cloth);
            switch (orderType) {
                case AUTO_EXCHANGE:
                    return acceptForAutoExchange(cloth, actualOrderType);
                case EXCHANGE_FOR_A_NEW_ONE:
                    return acceptForExchangeForNewOne(cloth, actualOrderType);
                default:
                    return ResponseClothAcceptance.wrongOrderTypeResponse(orderType);
            }
        }
    }


    public void updateClothes(
            List<Cloth> currentClothes, List<Cloth> actualClothes, Employee employee) {
        List<Cloth> newClothes = new LinkedList<>();
        if (currentClothes.isEmpty()) {
            for (Cloth cloth : actualClothes) {
                cloth.setEmployee(employee);
                newClothes.add(cloth);
            }
        } else {
            for (Cloth cloth : currentClothes) {
                if (!actualClothes.contains(cloth)) {
                    cloth.setActive(false);
                    continue;
                }
                for (Cloth actualCloth : actualClothes) {
                    if (!currentClothes.contains(actualCloth)) {
                        actualCloth.setEmployee(employee);
                        newClothes.add(actualCloth);
                    } else if (actualCloth.equals(cloth)) {
                        cloth.setLastWashing(actualCloth.getLastWashing());
                    }
                }
                clothesRepository.flush();
            }
        }
        clothesRepository.saveAll(newClothes);
    }

    private boolean clothIsNotPresent(long clientId, Cloth cloth) {
        return cloth.equals(null) || isClothBelongsToOtherClient(cloth, clientId);
    }

    private ResponseClothAcceptance acceptForExchangeForNewOne(Cloth cloth, OrderType orderType) {
        if (orderType.equals(EMPTY)) {
            Cloth clothForExchange = acceptForExchange(cloth);
            ClothOrder clothOrder = orderService.placeOne(
                    clothForExchange,
                    orderType,
                    clothForExchange.getArticle(),
                    clothForExchange.getSize());
            return ResponseClothAcceptance.createNewOrderAddedAndClothAcceptedResponse(clothOrder);
        } else if (orderType.equals(EXCHANGE_FOR_A_NEW_ONE)) {
            ClothOrder order = acceptClothAndUpdateOrder(cloth);
            return ResponseClothAcceptance.createClothAcceptedResponse(order);
        } else {
            return getResponseForAnotherActiveOrder(cloth, 0);
        }
    }

    private ResponseClothAcceptance acceptForAutoExchange(Cloth cloth, OrderType activeOrderType) {
        if (activeOrderType.equals(EMPTY)) {
            return ResponseClothAcceptance.createNoActiveOrderPresentResponse(cloth);
        } else {
            ClothOrder order = acceptClothAndUpdateOrder(cloth);
            return ResponseClothAcceptance.createClothAcceptedResponse(order);
        }
    }

    private ResponseClothAcceptance getResponseForAnotherActiveOrder(Cloth cloth, long barCode) {
        try {
            ClothOrder activeOrder = getOrderFromCloth(cloth);
            return ResponseClothAcceptance.createAnotherOrderIsActiveResponse(activeOrder);
        } catch (ClothOrderException e) {
            e.printStackTrace();
            e.getMessage();
            return ResponseClothAcceptance.createClothNotFound(barCode);
        }
    }

    private OrderType determineOrderType(Cloth cloth) {
        ClothOrder actualOrder = null;
        try {
            actualOrder = getOrderFromCloth(cloth);
        } catch (NoActiveClothOrderException e) {
            return EMPTY;
        } catch (ClothOrderException e) {
            e.printStackTrace();
            e.getMessage();
        }
        return actualOrder.getOrderType();
    }

    private ClothOrder acceptClothAndUpdateOrder(Cloth cloth) {
        Cloth acceptedCloth = acceptForExchange(cloth);
        ClothOrder clothOrder = setOrderReadyForRealization(acceptedCloth, user);
        return clothOrder;
    }

    private Cloth acceptForExchange(Cloth cloth) {
        cloth = clothesManager.updateCloth(ACCEPTED_FOR_EXCHANGE, cloth);
        cloth = clothesManager.updateOrder(cloth);
        return clothesRepository.save(cloth);
    }

    private ClothOrder setOrderReadyForRealization(Cloth cloth, User user) {
        ClothOrder order = null;
        OrderManager orderManager = new OrderManager(user);
        try {
            order = getOrderFromCloth(cloth);
            order = orderManager.update(
                    OrderStatus.OrderStage.READY_FOR_REALIZATION, order);
            ordersRepository.save(order);
        } catch (ClothOrderException e) {
            e.printStackTrace();
            e.getMessage();
        }
        return order;
    }

    private ClothOrder getOrderFromCloth(Cloth cloth) throws ClothOrderException {
        if (cloth.getClothOrder().equals(null)) {
            throw new NoActiveClothOrderException("");
        } else {
            ClothOrder clothOrder = cloth.getClothOrder();
            if (clothOrder.isActive()) {
                return clothOrder;
            } else {
                throw new NoActiveClothOrderException("");
            }
        }

    }

    private ResponseClothAcceptance createClothNotFoundResponse(Cloth cloth, long id) {
        if (cloth == null) {
            return ResponseClothAcceptance.createClothNotFound(id);
        }
        return ResponseClothAcceptance.createClothBelongsToOtherClient(cloth);
    }


    private boolean isClothBelongsToOtherClient(Cloth cloth, long clientId) {
        if (clientId == cloth.getClientId()) {
            return false;
        } else {
            return true;
        }
    }
}
