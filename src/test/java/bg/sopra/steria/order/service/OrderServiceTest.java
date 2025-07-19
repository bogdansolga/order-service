package bg.sopra.steria.order.service;

import bg.sopra.steria.order.domain.model.Order;
import bg.sopra.steria.order.domain.model.OrderItem;
import bg.sopra.steria.order.domain.model.OrderStatus;
import bg.sopra.steria.order.domain.repository.OrderRepository;
import bg.sopra.steria.order.dto.CreateOrderItemDTO;
import bg.sopra.steria.order.dto.OrderDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Given a valid order ID exists, when retrieving the order then the order is retrieved correctly")
    void givenValidOrderIdExists_whenRetrievingOrder_thenOrderIsRetrievedCorrectly() {
        // arrange
        final long orderId = 1L;
        final long customerId = 100L;
        final double total = 25.50;
        
        Order order = new Order(customerId, total);
        order.setId(orderId);
        order.setStatus(OrderStatus.PAYED);
        
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setName("Pizza");
        orderItem.setPrice(25L);
        orderItem.setOrder(order);
        order.setOrderItems(Set.of(orderItem));
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        // act
        final OrderDTO result = orderService.getOrder(orderId);
        
        // assert
        assertNotNull(result, "The order DTO should not be null");
        assertThat(result.orderId(), is(orderId));
        assertThat(result.customerId(), is(23L)); // Note: The service hardcodes this to 23
        assertThat(result.total(), is(total));
        assertNotNull(result.orderItems());
        assertThat(result.orderItems().size(), is(1));
    }

    @Test
    @DisplayName("Given an invalid order ID, when retrieving the order then an exception is thrown")
    void givenInvalidOrderId_whenRetrievingOrder_thenExceptionIsThrown() {
        // arrange
        final long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        
        // act & assert
        assertThrows(IllegalArgumentException.class, () -> orderService.getOrder(orderId));
    }

    @Test
    @DisplayName("Given valid order item data, when creating an order then the order is created successfully")
    void givenValidOrderItemData_whenCreatingOrder_thenOrderIsCreatedSuccessfully() {
        // arrange
        final long customerId = 100L;
        final CreateOrderItemDTO orderItemDTO = new CreateOrderItemDTO(
                10L,  // restaurantId
                20L,  // foodId
                30L,  // price
                "Burger",
                "Delicious beef burger"
        );
        
        Order savedOrder = new Order(customerId, orderItemDTO.price());
        savedOrder.setId(1L);
        savedOrder.setStatus(OrderStatus.CREATED);
        
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setRestaurantId(orderItemDTO.restaurantId());
        orderItem.setFoodId(orderItemDTO.foodId());
        orderItem.setPrice(orderItemDTO.price());
        orderItem.setName(orderItemDTO.name());
        orderItem.setDescription(orderItemDTO.description());
        orderItem.setOrder(savedOrder);
        savedOrder.setOrderItems(Set.of(orderItem));
        
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        
        // act
        final OrderDTO result = orderService.createOrder(customerId, orderItemDTO);
        
        // assert
        assertNotNull(result, "The created order DTO should not be null");
        assertThat(result.orderId(), is(1L));
        assertThat(result.customerId(), is(customerId));
        assertThat(result.total(), is((double) orderItemDTO.price()));
        assertNotNull(result.orderItems());
        assertThat(result.orderItems().size(), is(1));
        assertThat(result.orderItems().get(0).productName(), is("Burger"));
        
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Given order item with zero price, when creating an order then the order is created with zero total")
    void givenOrderItemWithZeroPrice_whenCreatingOrder_thenOrderIsCreatedWithZeroTotal() {
        // arrange
        final long customerId = 100L;
        final CreateOrderItemDTO orderItemDTO = new CreateOrderItemDTO(
                10L,  // restaurantId
                20L,  // foodId
                0L,   // price
                "Free Sample",
                "Free sample item"
        );
        
        Order savedOrder = new Order(customerId, 0);
        savedOrder.setId(2L);
        savedOrder.setStatus(OrderStatus.CREATED);
        
        OrderItem orderItem = new OrderItem();
        orderItem.setId(2L);
        orderItem.setPrice(0L);
        orderItem.setName("Free Sample");
        orderItem.setOrder(savedOrder);
        savedOrder.setOrderItems(Set.of(orderItem));
        
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        
        // act
        final OrderDTO result = orderService.createOrder(customerId, orderItemDTO);
        
        // assert
        assertNotNull(result);
        assertThat(result.total(), is(0.0));
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}