package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.service.OrderService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/order")
public class OrderController {
    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @JsonView(Views.InternalForClothOrders.class)
    @PostMapping("/place/{articleNumber}/{size}/{orderType}/{userId}")
    public List<ClothOrder> place(@PathVariable int articleNumber, @PathVariable ClothSize size,
                                  @PathVariable OrderType orderType, @PathVariable long userId,
                                  @RequestBody long[] clothIds) {
        return orderService.place(articleNumber, size, orderType, userId, clothIds);
    }

    @JsonView(Views.InternalForClothOrders.class)
    @GetMapping("/get-by-employee/{employeeId}")
    public Set<ClothOrder> getByEmployeeId(@PathVariable long employeeId) {
        return orderService.getByEmployeeId(employeeId);
    }

    @PostMapping("/action/{actionType}/{userId}")
    @JsonView(Views.InternalForClothOrders.class)
    public Set<ClothOrder> performActionOnOrders(
            @PathVariable ActionType actionType, @PathVariable long userId,
            @RequestBody long[] clothOrderIds) {
        return orderService.performActionOnOrders(actionType, userId, clothOrderIds);
    }
}
