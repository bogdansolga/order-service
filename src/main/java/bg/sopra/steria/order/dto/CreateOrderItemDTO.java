package bg.sopra.steria.order.dto;

import java.io.Serializable;

public record CreateOrderItemDTO(
        long restaurantId,
        long foodId,
        long price,
        String name,
        String description
) implements Serializable {
}