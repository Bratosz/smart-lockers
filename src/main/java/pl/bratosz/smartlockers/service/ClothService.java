package pl.bratosz.smartlockers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.ClothOrderException;
import pl.bratosz.smartlockers.exception.NoActiveClothOrderException;
import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.clothes.ClothStatus;
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
import static pl.bratosz.smartlockers.model.orders.OrderStatus.OrderStage.READY_FOR_REALIZATION;
import static pl.bratosz.smartlockers.model.orders.OrderType.*;

@Service
public class ClothService {
    private ClothesRepository clothesRepository;
    private OrdersRepository ordersRepository;
    @Autowired
    private OrderService orderService;
    private OrderStatusService orderStatusService;
    private UserService userService;
    private OrderManager orderManager;
    private ClothStatusService clothStatusService;
    private User user;
    private ClothesManager clothesManager;

    public ClothService(ClothesRepository clothesRepository,
                        OrdersRepository ordersRepository,
                        OrderStatusService orderStatusService, UserService userService,
                        OrderManager orderManager, ClothStatusService clothStatusService,
                        ClothesManager clothesManager) {
        this.clothesRepository = clothesRepository;
        this.ordersRepository = ordersRepository;
        this.orderStatusService = orderStatusService;
        this.userService = userService;
        this.orderManager = orderManager;
        this.clothStatusService = clothStatusService;
        this.clothesManager = clothesManager;
    }

    private void loadUser(User user) {
        this.user = user;
    }

    private void loadUser(long userId) {
        this.user = userService.getUserById(userId);
    }

    public Cloth createNewInstead(
            int ordinalNumber,
            Article article,
            ClothSize size,
            Employee employee,
            User user
    ) {
        loadUser(user);
        Cloth newCloth = clothesManager.createNewInstead(ordinalNumber, article, size, employee);
        return clothesRepository.save(newCloth);
    }

    public List<Cloth> getByIds(long[] clothsIds) {
        List<Cloth> clothes = new LinkedList<>();
        for (int i = 0; i < clothsIds.length; i++) {
            long clothId = clothsIds[i];
            Cloth cloth = clothesRepository.getClothById(clothId);
            clothes.add(cloth);
        }
        return clothes;
    }

    public List<Cloth> getByBarCodes(long[] barCodes) {
        List<Cloth> clothes = new LinkedList<>();
        for(long barCode : barCodes) {
            Cloth cloth = clothesRepository.getClothByBarCode(barCode);
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

    public ResponseClothAcceptance accept(long clientId, long userId, long clothBarCode, OrderType orderType) {
        loadUser(userId);
        Cloth cloth = clothesRepository.getClothByBarCode(clothBarCode);
        if (clothIsNotPresent(clientId, cloth)) {
            return createClothNotFoundResponse(cloth, clothBarCode);
        } else if(clothIsReturned(cloth)) {
            return ResponseClothAcceptance.createClothAlreadyReturned(cloth);
        } else {
            OrderType actualOrderType = getActualOrderType(cloth);
            switch (orderType) {
                case AUTO_EXCHANGE:
                    return acceptForAutoExchange(cloth, actualOrderType);
                case EXCHANGE_FOR_A_NEW_ONE:
                    return acceptForExchangeForNewOne(orderType, cloth, actualOrderType);
                default:
                    return ResponseClothAcceptance.wrongOrderTypeResponse(orderType);
            }
        }
    }

    private boolean clothIsReturned(Cloth cloth) {
        if(cloth.getClothStatus().getActualStatus().equals(ACCEPTED_FOR_EXCHANGE)) {
            return true;
        } else {
            return false;
        }
    }


    public void updateClothes(
            List<Cloth> currentClothes,
            List<Cloth> actualClothes,
            Employee employee,
            User user) {
        loadUser(user);
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
        return cloth == null || isClothBelongsToOtherClient(cloth, clientId);
    }

    private ResponseClothAcceptance acceptForExchangeForNewOne(OrderType orderType, Cloth cloth, OrderType actualOrderType) {
        if (actualOrderType.equals(EMPTY)) {
            Cloth clothForExchange = acceptForExchange(cloth);
            OrderStatus orderStatus = orderStatusService.create(
                    READY_FOR_REALIZATION,
                    user);
            ClothOrder clothOrder = orderService.placeOne(
                    clothForExchange,
                    orderType,
                    orderStatus,
                    clothForExchange.getArticle(),
                    clothForExchange.getSize(),
                    user);
            return ResponseClothAcceptance.createNewOrderAddedAndClothAcceptedResponse(clothOrder);
        } else if (actualOrderType.equals(EXCHANGE_FOR_A_NEW_ONE)) {
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

    private OrderType getActualOrderType(Cloth cloth) {
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
        ClothStatus actualStatus = clothStatusService.create(ACCEPTED_FOR_EXCHANGE, cloth, user);
        cloth = clothesManager.updateCloth(actualStatus, cloth);
        if(cloth.getExchangeOrder() != null) cloth =
                clothesManager.updateOrder(cloth, user);
        return clothesRepository.save(cloth);
    }

    private ClothOrder setOrderReadyForRealization(Cloth cloth, User user) {
        ClothOrder order = null;
        try {
            order = getOrderFromCloth(cloth);
            order = orderManager.update(
                    READY_FOR_REALIZATION, order, user);
            ordersRepository.save(order);
        } catch (ClothOrderException e) {
            e.printStackTrace();
            e.getMessage();
        }
        return order;
    }

    private ClothOrder getOrderFromCloth(Cloth cloth) throws ClothOrderException {
        if (cloth.getExchangeOrder() == null) {
            throw new NoActiveClothOrderException("");
        } else {
            ClothOrder clothOrder = cloth.getExchangeOrder();
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

    public List<Cloth> createExisting(List<Cloth> clothes, User user) {
        List<Cloth> existingClothes = new LinkedList<>();
        clothes = clothesRepository.saveAll(clothes);
        for(Cloth cloth : clothes) {
            existingClothes.add(
                    clothesManager.createExisting(cloth, user));
        }
        return existingClothes;
    }
}
