package bg.sopra.steria.order.controller;

import bg.sopra.steria.order.dto.CreateOrderItemDTO;
import bg.sopra.steria.order.dto.OrderDTO;
import bg.sopra.steria.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    public OrderDTO getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }

    @PostMapping("/{customerId}")
    public OrderDTO createOrder(@PathVariable Long customerId, @RequestBody CreateOrderItemDTO orderItem) {
        return orderService.createOrder(customerId, orderItem);
    }
}
