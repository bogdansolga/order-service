package bg.sopra.steria.order.dto;

import java.io.Serializable;
import java.util.List;

public record OrderDTO(long customerId, long orderId, double total, List<OrderItemDTO> orderItems) implements Serializable {

    public double getOrderTotal() {
        return orderItems.stream()
                .mapToDouble(OrderItemDTO::productPrice)
                .sum();
    }

}
