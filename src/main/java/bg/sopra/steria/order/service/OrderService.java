package bg.sopra.steria.order.service;

import bg.sopra.steria.order.domain.model.Order;
import bg.sopra.steria.order.domain.model.OrderItem;
import bg.sopra.steria.order.domain.model.OrderStatus;
import bg.sopra.steria.order.domain.repository.OrderRepository;
import bg.sopra.steria.order.dto.CreateOrderItemDTO;
import bg.sopra.steria.order.dto.OrderDTO;
import bg.sopra.steria.order.dto.OrderItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private static final Random RANDOM = new Random();

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void saveInitialOrder() {
        Order order = new Order(1, RANDOM.nextInt(200));
        order.setStatus(OrderStatus.PAYED);

        OrderItem orderItem = new OrderItem();
        orderItem.setRestaurantId(RANDOM.nextInt(100));
        orderItem.setFoodId(RANDOM.nextLong(150));
        orderItem.setPrice(RANDOM.nextLong(200));
        orderItem.setName("Great pizza");
        orderItem.setDescription("A delicious pizza");

        order.setOrderItems(Set.of(orderItem));
        orderItem.setOrder(order);
        orderRepository.save(order);
        LOGGER.info("The initial order was saved");
    }

    @Transactional(readOnly = true,  propagation = Propagation.REQUIRED)
    public OrderDTO getOrder(final long orderId) {
        Order order = getOrderByIdOrElseThrow(orderId);
        return new OrderDTO(23, order.getId(), order.getTotal(), convertOrderItems(order.getOrderItems()));
    }

    private Order getOrderByIdOrElseThrow(long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("There is no order with the ID " + orderId));
    }

    private List<OrderItemDTO> convertOrderItems(Set<OrderItem> orderItems) {
        return orderItems.stream()
                         .map(item -> new OrderItemDTO(item.getId(), item.getName(), item.getPrice()))
                         .toList();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public OrderDTO createOrder(long customerId, CreateOrderItemDTO item) {
        LOGGER.info("Creating order for customer {}", customerId);
        
        Order order = new Order(customerId, item.price());
        order.setStatus(OrderStatus.CREATED);
        
        OrderItem orderItem = new OrderItem();
        orderItem.setRestaurantId(item.restaurantId());
        orderItem.setFoodId(item.foodId());
        orderItem.setPrice(item.price());
        orderItem.setName(item.name());
        orderItem.setDescription(item.description());
        orderItem.setOrder(order);
        
        order.setOrderItems(Set.of(orderItem));
        Order savedOrder = orderRepository.save(order);
        
        LOGGER.info("Order {} created successfully", savedOrder.getId());
        return new OrderDTO(savedOrder.getCustomerId(), savedOrder.getId(), savedOrder.getTotal(), convertOrderItems(savedOrder.getOrderItems()));
    }
}
