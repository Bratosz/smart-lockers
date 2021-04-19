package pl.bratosz.smartlockers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.ClothOrderException;
import pl.bratosz.smartlockers.exception.NoActiveClothOrderException;
import pl.bratosz.smartlockers.model.clothes.*;
import pl.bratosz.smartlockers.model.orders.*;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.OrdersRepository;
import pl.bratosz.smartlockers.response.ResponseClothAcceptance;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.ClothesRepository;

import pl.bratosz.smartlockers.response.ResponseClothAssignment;
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
    private EmployeeService employeeService;
    private ClothStatusService clothStatusService;
    private ArticleTypeService articleTypeService;
    private User user;
    private ClothesManager clothesManager;

    public ClothService(ClothesRepository clothesRepository,
                        OrdersRepository ordersRepository,
                        OrderStatusService orderStatusService,
                        UserService userService,
                        OrderManager orderManager,
                        @Lazy EmployeeService employeeService,
                        ClothStatusService clothStatusService,
                        ArticleTypeService articleTypeService, ClothesManager clothesManager) {
        this.clothesRepository = clothesRepository;
        this.ordersRepository = ordersRepository;
        this.orderStatusService = orderStatusService;
        this.userService = userService;
        this.orderManager = orderManager;
        this.employeeService = employeeService;
        this.clothStatusService = clothStatusService;
        this.articleTypeService = articleTypeService;
        this.clothesManager = clothesManager;
    }

    private void loadUser(User user) {
        this.user = user;
    }

    private void loadUser(long userId) {
        this.user = userService.getUserById(userId);
    }

    public Cloth createNewForAssignInsteadExisting(
            int ordinalNumber,
            ArticleType articleType,
            ClothSize size,
            Employee employee,
            User user
    ) {
        loadUser(user);
        Cloth newCloth = clothesManager.createNewInstead(ordinalNumber, articleType, size, employee);
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

    public List<Cloth> getByBarcodes(long[] barCodes) {
        List<Cloth> clothes = new LinkedList<>();
        for (long barCode : barCodes) {
            Cloth cloth = clothesRepository.getByBarcode(barCode);
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

    public ResponseClothAssignment assignWithdrawnCloth(long clientId,
                                                        long userId,
                                                        long employeeId,
                                                        int articleNumber,
                                                        ClothSize size,
                                                        Cloth withdrawnCloth) {
        loadUser(userId);
        Employee employee = employeeService.getById(employeeId);
        Cloth clothByBarcode = clothesRepository.getByBarcode(withdrawnCloth.getBarcode());
        if (clothIsPresent(clothByBarcode, clientId)) {
            return ResponseClothAssignment.createForFailure(
                    "Ubranie jest aktywne");
        } else if (clothBelongsToOtherClient(clothByBarcode, clientId)) {
            return ResponseClothAssignment.createForFailure(
                    "Ubranie należy do innego klienta");
        } else {
            withdrawnCloth.setSize(size);
            withdrawnCloth.setArticleType(articleTypeService.get(articleNumber));
            return assignAsWithdrawnCloth(withdrawnCloth, employee);
        }
    }


    public ResponseClothAssignment releaseRotationalCloth(long clientId, long userId, long employeeId, long barcode) {
        loadUser(userId);
        Employee employee = employeeService.getById(employeeId);
        Cloth clothByBarcode = clothesRepository.getByBarcode(barcode);
        if (clothIsPresent(clothByBarcode, clientId)) {
            return releaseAsRotational(clothByBarcode, employee);
        } else {
            return ResponseClothAssignment.createForFailure(
                    "Brak ubrania o podanym kodzie kreskowym");
        }
    }

    private ResponseClothAssignment releaseAsRotational(Cloth clothByBarcode, Employee employee) {
        if(!clothIsRotational(clothByBarcode)) {
            return ResponseClothAssignment.createForFailure("To nie jest odzież rotacyjna");
        } else if(!clothCanBeReleasedAsRotational(clothByBarcode)) {
            return ResponseClothAssignment.createForFailure(
                    "Ta odzież została już wydana");
        } else {
            RotationalCloth rotationalCloth = (RotationalCloth) clothByBarcode;
            rotationalCloth.setRotationTemporaryOwner(employee);
            clothesRepository.save(rotationalCloth);
            return ResponseClothAssignment.createForSucceed();
        }
    }

    private boolean clothCanBeReleasedAsRotational(Cloth cloth) {
        if(clothIsRotational(cloth)) {
            RotationalCloth rotationalCloth = (RotationalCloth) cloth;
            return rotationalCloth.isReturned();
        } else {
            return false;
        }
    }

    private boolean clothIsRotational(Cloth cloth) {
        return cloth.getClass().isInstance(RotationalCloth.class);
    }

    private ResponseClothAssignment assignAsWithdrawnCloth(Cloth withdrawnCloth, Employee employee) {
        ClothStatus clothStatus = clothStatusService.create(ClothDestination.FOR_DISPOSAL, user);
        withdrawnCloth.setStatus(clothStatus);
        withdrawnCloth.setEmployee(employee);
        clothesRepository.save(withdrawnCloth);
        return ResponseClothAssignment.createForSucceed();
    }

    public ResponseClothAcceptance accept(long clientId,
                                          long userId,
                                          long clothBarCode,
                                          OrderType orderType) {
        loadUser(userId);
        Cloth cloth = clothesRepository.getByBarcode(clothBarCode);
        if (clothIsAbsent(clientId, cloth)) {
            return createClothNotFoundResponse(cloth, clothBarCode);
        } else if (clothIsReturned(cloth)) {
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

    private boolean clothIsPresent(Cloth cloth, long clientId) {
        return !clothIsAbsent(clientId, cloth);
    }

    private boolean clothIsReturned(Cloth cloth) {
        if (cloth.getClothStatus().getActualStatus().equals(ACCEPTED_FOR_EXCHANGE)) {
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

    private boolean clothIsAbsent(long clientId, Cloth cloth) {
        return cloth == null ||
                clothBelongsToOtherClient(cloth, clientId);
    }

    private ResponseClothAcceptance acceptForExchangeForNewOne(OrderType orderType,
                                                               Cloth cloth,
                                                               OrderType actualOrderType) {
        if (actualOrderType.equals(EMPTY)) {
            Cloth clothForExchange = acceptForExchange(cloth);
            OrderStatus orderStatus = orderStatusService.create(
                    READY_FOR_REALIZATION,
                    user);
            ClothOrder clothOrder = orderService.placeOne(
                    clothForExchange,
                    orderType,
                    orderStatus,
                    clothForExchange.getArticleType(),
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

    private ResponseClothAcceptance acceptForAutoExchange(Cloth cloth, OrderType actualOrderType) {
        if (actualOrderType.equals(EMPTY)) {
            return ResponseClothAcceptance.createNoActiveOrderPresentResponse(cloth);
        } else {
            ClothOrder order = acceptClothAndUpdateOrder(cloth);
            return ResponseClothAcceptance.createClothAcceptedResponse(order);
        }
    }

    private ResponseClothAcceptance getResponseForAnotherActiveOrder(Cloth cloth, long barCode) {
        try {
            ClothOrder activeOrder = getOrder(cloth);
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
            actualOrder = getOrder(cloth);
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
        try {
            return orderService.setOrderReadyForRealization(
                    getOrder(acceptedCloth), user);
        } catch (ClothOrderException e) {
            e.printStackTrace();
            return orderService.getEmpty();
        }
    }

    private Cloth acceptForExchange(Cloth cloth) {
        ClothStatus actualStatus = clothStatusService.create(ACCEPTED_FOR_EXCHANGE, cloth, user);
        cloth = clothesManager.updateCloth(actualStatus, cloth);
        return clothesRepository.save(cloth);
    }

    private ClothOrder getOrder(Cloth cloth) throws ClothOrderException {
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


    private boolean clothBelongsToOtherClient(Cloth cloth, long clientId) {
        if (cloth != null && clientId != cloth.getClientId()) {
            return true;
        } else {
            return false;
        }
    }

    public List<Cloth> createExisting(List<Cloth> clothes) {
        return clothesRepository.saveAll(clothes);
    }

    public void hardDelete(Cloth cloth) {
        List<ClothStatus> statusHistory = cloth.getStatusHistory();
        clothStatusService.hardDelete(statusHistory);
        clothesRepository.deleteById(cloth.getId());

    }


}
