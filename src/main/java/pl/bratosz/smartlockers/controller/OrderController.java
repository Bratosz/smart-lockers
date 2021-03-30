package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.orders.ActionType;
import pl.bratosz.smartlockers.model.orders.ClothOrder;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.response.ResponseOrdersCreated;
import pl.bratosz.smartlockers.service.OrderService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @JsonView(Views.InternalForClothOrders.class)
    @PostMapping("/place/{articleNumber}/{size}/{orderType}/{userId}")
    public ResponseOrdersCreated place(@PathVariable OrderType orderType,
                                       @PathVariable int articleNumber,
                                       @PathVariable ClothSize size,
                                       @RequestBody long[] barCodes,
                                       @PathVariable long userId) {
        return orderService.placeMany(
                orderType,
                articleNumber,
                size,
                barCodes,
                userId);
    }

    @JsonView(Views.InternalForClothOrders.class)
    @GetMapping("/get-by-employee/{employeeId}")
    public Set<ClothOrder> getByEmployeeId(@PathVariable long employeeId) {
        return orderService.getByEmployeeId(employeeId);
    }

    @PostMapping("/action/{actionType}/{userId}")
    @JsonView(Views.InternalForClothOrders.class)
    public List<ClothOrder> performActionOnOrders(
            @PathVariable ActionType actionType,
            @RequestBody long[] clothOrderIds,
            @PathVariable long userId) {
        return orderService.performActionOnOrders(
                actionType,
                clothOrderIds,
                userId);
    }
}
