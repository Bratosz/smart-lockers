package pl.bratosz.smartlockers.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.ClothOrderException;
import pl.bratosz.smartlockers.exception.NoActiveClothOrderException;
import pl.bratosz.smartlockers.repository.OrdersRepository;
import pl.bratosz.smartlockers.response.ResponseClothAcceptance;
import pl.bratosz.smartlockers.service.exels.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.ClothesRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static pl.bratosz.smartlockers.service.exels.LoadType.*;

@Service
public class ClothService {
    private EmployeeService employeeService;
    private ClothesRepository clothesRepository;
    private OrdersRepository ordersRepository;
    @Autowired
    private OrderService orderService;

    public ClothService(EmployeeService employeeService, ClothesRepository clothesRepository,
                        OrdersRepository ordersRepository) {
        this.employeeService = employeeService;
        this.clothesRepository = clothesRepository;
        this.ordersRepository = ordersRepository;
    }

    public List<Cloth> uploadClothesRotation(Sheet sheet, LoadType loadType) {
        RowLoader clothesReader = RowLoader.create(loadType);
        List<RowForRotationUpdate> rowsForRotationUpdate = clothesReader.loadRows(sheet);
        return loadClothesRotation(rowsForRotationUpdate);
    }

    public List<Cloth> updateReleasedRotation(Sheet sheet, LoadType loadType) {
        if (loadType.equals(RELEASED_ROTATIONAL_CLOTHING_UPDATE)) {
            RowLoader clothesReader = RowLoader.create(loadType);
            List<RowForReleasedRotationalClothes> loadedRotationalClothing = clothesReader.loadRows(sheet);
            return updateClothesRotation(loadedRotationalClothing);
        }
        return null;
    }

    private List<Cloth> updateClothesRotation(List<RowForReleasedRotationalClothes> clothRows) {
        List<Cloth> clothes = new LinkedList<>();
        List<Cloth> all = findAllClothesByRotationalValue(true);
        for (RowForReleasedRotationalClothes row : clothRows) {
            if (all.stream().anyMatch(r -> r.getId() == row.getBarCode())) {
                Cloth cloth = clothesRepository.getOne(row.getBarCode());
                Employee employee = employeeService.getOneEmployee(row.getFirstName(), row.getLastName());
                if (employee.getId() == 1) {
                    continue;
                }
                cloth.setReleaseAsRotationalDate(row.getReleaseDate());
                cloth.setRotationOwner(employee);
                clothes.add(cloth);
            }
        }
        return clothesRepository.saveAll(clothes);
    }

    private List<Cloth> findAllClothesByRotationalValue(boolean isRotational) {
        List<Cloth> clothes = clothesRepository.findAllClothesByRotationalValue(isRotational);
        return clothes;
    }


    private List<Cloth> loadClothesRotation(List<RowForRotationUpdate> rowForRotationUpdate) {
        List<Cloth> clothes = new LinkedList<>();
        for (RowForRotationUpdate row : rowForRotationUpdate) {
            Cloth cloth = createCloth(row);
            clothes.add(cloth);
        }
        return clothesRepository.saveAll(clothes);

    }

    private Cloth createCloth(RowForRotationUpdate rowForRotationUpdate) {

        if (isClothRotational(rowForRotationUpdate.getLockerNo())) {
            return new Cloth();
        } else {
            Employee employee = employeeService.getEmployeeByFullNameAndFullBoxNumber(
                    rowForRotationUpdate.getFirstName(),
                    rowForRotationUpdate.getLastName(),
                    rowForRotationUpdate.getLockerNo(),
                    rowForRotationUpdate.getBoxNo()
            );
            return new Cloth();
        }
    }

    private boolean isClothRotational(int lockerNo) {
        if (lockerNo == 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cloth getClothById(long id) {
        return clothesRepository.getOne(id);
    }

    public List<Cloth> getRotationalClothRaport() throws IOException {
        List<Cloth> rotationalClothes = getReleasedRotationalClothes();
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Rotacja do zwrócenia");
        Row row;
        for (int i = 0; i < rotationalClothes.size(); i++) {
            row = sheet.createRow(i);
            Cloth cloth = rotationalClothes.get(i);
            Employee emp = cloth.getEmployee();
            row.createCell(0).setCellValue(emp.getId());
            row.createCell(1).setCellValue(emp.getFirstName());
            row.createCell(2).setCellValue(emp.getLastName());
            row.createCell(3).setCellValue(emp.getBox().getLocker().getLockerNumber());
            row.createCell(4).setCellValue(emp.getBox().getBoxNumber());
            row.createCell(5).setCellValue(emp.getDepartment().getName());
            row.createCell(6).setCellValue(cloth.getId());
            row.createCell(7).setCellValue(cloth.getArticle().getName());
            row.createCell(8).setCellValue(cloth.getArticle().getArticleNumber());
        }
        FileOutputStream fileOut = new FileOutputStream("C:/Users/HP/Desktop/files_to_testing/Lear/raports/rotacja_do_zwrotu.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
        return rotationalClothes;
    }

    private List<Cloth> getReleasedRotationalClothes() {
        return clothesRepository.getReleasedRotationalClothes();
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

    public ClothSize determineDesiredSize(ClothSize desiredSize, ClothSize actualSize) {
        if (desiredSize == ClothSize.SIZE_DEFAULT) {
            return actualSize;
        } else {
            return desiredSize;
        }
    }

    public ResponseClothAcceptance accept(long clientId, long userId, long clothId, OrderType orderType) {
        Cloth cloth = clothesRepository.getClothById(clothId);
        if(isClothNOTPresent(clientId, cloth)){
            return createClothNotFoundResponse(cloth, clothId);
        } else {
            OrderType activeOrderType = getActiveOrderType(cloth);
            switch(orderType) {
                case AUTO_EXCHANGE:
                    return acceptForAutoExchange(cloth, activeOrderType);
                case EXCHANGE_FOR_A_NEW_ONE:
                    return acceptForExchangeForNewOne(userId, cloth, activeOrderType);
                default:
                    return ResponseClothAcceptance.wrongOrderTypeResponse(orderType);
            }
        }
    }

    public ResponseClothAcceptance acceptWithOrderCreation(
            long userId, long clientId, long clothId, OrderType orderType,
            ClothAcceptanceType acceptanceType, ClothSize size, int articleNumber) {
        Cloth cloth = clothesRepository.getClothById(clothId);
        if (isClothNOTPresent(clientId, cloth)) {
            return createClothNotFoundResponse(cloth, clothId);
        } else {
            switch (acceptanceType) {
                case EXCHANGE:
                    return acceptForExchange(userId, cloth, orderType, size, articleNumber);
                case REPAIR:
                    return acceptForRepair(userId, cloth);
                case WASHING:
                    return acceptForWashing(cloth);
                default:
                    return ResponseClothAcceptance.createWrongAcceptanceTypeResponse(acceptanceType);
            }
        }
    }

    public void updateClothes(
            Set<Cloth> currentClothes, Set<Cloth> actualClothes, Employee employee) {
        Set<Cloth> newClothes = new HashSet<>();
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

    public void updateClothes(Set<Cloth> actualClothes, Employee employee) {
        Set<Cloth> currentClothes = new HashSet<>();
        updateClothes(currentClothes, actualClothes, employee);
    }

    private boolean isClothNOTPresent(long clientId, Cloth cloth) {
        return cloth == null || isClothBelongsToOtherClient(cloth, clientId);
    }

    private ResponseClothAcceptance acceptForRepair(long userId, Cloth cloth) {
        return null;
    }

    private ResponseClothAcceptance acceptForExchange(
            long userId, Cloth cloth, OrderType orderType, ClothSize size, int articleNumber) {
        OrderType activeOrderType = getActiveOrderType(cloth);
        switch (orderType) {
            case AUTO_EXCHANGE:
                return acceptForAutoExchange(cloth, activeOrderType);
            case CHANGE_SIZE:
                return acceptForChangeSize(userId, cloth, size, activeOrderType);
            case EXCHANGE_FOR_A_NEW_ONE:
                return acceptForExchangeForNewOne(userId, cloth, activeOrderType);
            case CHANGE_ARTICLE:
                return acceptForChangeArticle(userId, cloth, size, articleNumber, activeOrderType);
            default:
                return ResponseClothAcceptance.createClothNotFound(0);
        }
    }

    private ResponseClothAcceptance acceptForChangeArticle(long userId, Cloth cloth, ClothSize size, int articleNumber, OrderType activeOrderType) {
        if(activeOrderType.equals(OrderType.NO_ACTIVE_ORDER)) {
            Cloth acceptedCloth = acceptCloth(cloth);
            ClothOrder clothOrder = orderService.createOrderForChangeArticle(acceptedCloth, articleNumber,
                    size, OrderStatus.ACCEPTED_AND_READY_FOR_REALIZATION, new Date(), userId);
            return ResponseClothAcceptance.createNewOrderAddedAndClothAcceptedResponse(clothOrder);
        } else if (activeOrderType.equals(OrderType.CHANGE_ARTICLE)) {
                ClothOrder order = acceptClothAndUpdateOrder(cloth);
                return ResponseClothAcceptance.createClothAcceptedResponse(order);
        } else {
            return getResponseForAnotherActiveOrder(cloth, 0);
        }
    }

    private ResponseClothAcceptance acceptForExchangeForNewOne(long userId, Cloth cloth, OrderType activeOrderType) {
        if(activeOrderType.equals(OrderType.NO_ACTIVE_ORDER)) {
            Cloth acceptedCloth = acceptCloth(cloth);
            ClothOrder clothOrder = orderService.createOrderForExchangeForNewOne(acceptedCloth,
                    OrderStatus.ACCEPTED_AND_READY_FOR_REALIZATION, new Date(), userId);
            return ResponseClothAcceptance.createNewOrderAddedAndClothAcceptedResponse(clothOrder);
        } else if (activeOrderType.equals(OrderType.EXCHANGE_FOR_A_NEW_ONE)) {
            ClothOrder order = acceptClothAndUpdateOrder(cloth);
            return ResponseClothAcceptance.createClothAcceptedResponse(order);
        } else {
            return getResponseForAnotherActiveOrder(cloth, 0);
        }
    }

    private ResponseClothAcceptance acceptForChangeSize(long userId, Cloth cloth, ClothSize size, OrderType activeOrderType) {
        if (activeOrderType.equals(OrderType.NO_ACTIVE_ORDER)) {
            Cloth acceptedCloth = acceptCloth(cloth);
            ClothOrder clothOrder = orderService.createOrderForChangeSize(acceptedCloth, size,
                    OrderStatus.ACCEPTED_AND_READY_FOR_REALIZATION, new Date(), userId);
            return ResponseClothAcceptance.createNewOrderAddedAndClothAcceptedResponse(clothOrder);
        } else if (activeOrderType.equals(OrderType.CHANGE_SIZE)) {
            ClothOrder order = acceptClothAndUpdateOrder(cloth);
            return ResponseClothAcceptance.createClothAcceptedResponse(order);
        } else {
            return getResponseForAnotherActiveOrder(cloth, 0);
        }
    }

    private ResponseClothAcceptance acceptForAutoExchange(Cloth cloth, OrderType activeOrderType) {
        if(activeOrderType.equals(OrderType.NO_ACTIVE_ORDER)) {
            return ResponseClothAcceptance.createNoActiveOrderPresentResponse(cloth);
        } else {
            ClothOrder order = acceptClothAndUpdateOrder(cloth);
            return ResponseClothAcceptance.createClothAcceptedResponse(order);
        }
    }

    private ResponseClothAcceptance getResponseForAnotherActiveOrder(Cloth cloth, long barCode) {
        try {
            ClothOrder activeOrder = getActiveOrderFromCloth(cloth);
            return ResponseClothAcceptance.createAnotherOrderIsActiveResponse(activeOrder);
        } catch (ClothOrderException e) {
            e.printStackTrace();
            e.getMessage();
            return ResponseClothAcceptance.createClothNotFound(barCode);
        }
    }

    private OrderType getActiveOrderType(Cloth cloth) {
        ClothOrder activeOrder = null;
        try {
            activeOrder = getActiveOrderFromCloth(cloth);
        } catch (NoActiveClothOrderException e) {
            return OrderType.NO_ACTIVE_ORDER;
        } catch (ClothOrderException e) {
            e.printStackTrace();
            e.getMessage();
        }
        return activeOrder.getOrderType();
    }

    private ClothOrder acceptClothAndUpdateOrder(Cloth cloth) {
        Cloth acceptedCloth = acceptCloth(cloth);
        ClothOrder clothOrder = setOrderReadyForRealization(acceptedCloth);
        return clothOrder;
    }

    private Cloth acceptCloth(Cloth cloth) {
        cloth.setAcceptedForExchangeDate(new Date());
        cloth.setAcceptedForExchange(true);
        cloth.setAcceptedOwner(cloth.getEmployee());
        return clothesRepository.save(cloth);
    }

    private ClothOrder setOrderReadyForRealization(Cloth cloth) {
        ClothOrder activeOrder = null;
        try {
            activeOrder = getActiveOrderFromCloth(cloth);
            activeOrder.setReadyForRealization(true);
            activeOrder.setOrderStatus(OrderStatus.ACCEPTED_AND_READY_FOR_REALIZATION);
            ordersRepository.save(activeOrder);
        } catch (ClothOrderException e) {
            e.printStackTrace();
            e.getMessage();
        }
        return activeOrder;
    }

    private ClothOrder getActiveOrderFromCloth(Cloth cloth) throws ClothOrderException {
        ClothOrder clothOrder = null;
        Set<ClothOrder> clothOrders = cloth.getClothOrders();
        if(clothOrders.size() > 0) {
            clothOrders = cloth.getClothOrders();
            int activeOrdersCounter = 0;
            for (ClothOrder c : clothOrders) {
                if (c.isActive()) {
                    activeOrdersCounter++;
                    clothOrder = c;
                }
                if (activeOrdersCounter > 1) {
                    throw new ClothOrderException("Active orders amount: " + activeOrdersCounter);
                }
            }
            return clothOrder;
        } throw  new NoActiveClothOrderException("There is no active order");
    }

    private boolean isClothHaveActiveOrder(Cloth cloth) {
        if (cloth.getClothOrders().equals(null))
            return false;
        Set<ClothOrder> clothOrders = cloth.getClothOrders();
        boolean activeOrderPresent = false;
        for (ClothOrder c : clothOrders)
            if (c.isActive()) activeOrderPresent = true;
        return activeOrderPresent;
    }

    private ResponseClothAcceptance acceptForWashing(Cloth cloth) {
        return null;
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
