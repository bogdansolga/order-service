package bg.sopra.steria.order.dto;

import java.io.Serializable;
import java.util.Objects;

public record OrderItemDTO(long orderItemId, String productName, double productPrice) implements Serializable {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemDTO that = (OrderItemDTO) o;
        return orderItemId == that.orderItemId &&
                productPrice == that.productPrice &&
                Objects.equals(productName, that.productName);
    }

}
