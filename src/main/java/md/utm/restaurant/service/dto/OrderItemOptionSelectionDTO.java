package md.utm.restaurant.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link md.utm.restaurant.domain.OrderItemOptionSelection} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderItemOptionSelectionDTO implements Serializable {

    private Long id;

    @Min(value = 1)
    private Integer quantity;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal unitPrice;

    private MenuItemOptionValueDTO optionValue;

    @NotNull
    private OrderItemDTO orderItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public MenuItemOptionValueDTO getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(MenuItemOptionValueDTO optionValue) {
        this.optionValue = optionValue;
    }

    public OrderItemDTO getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItemDTO orderItem) {
        this.orderItem = orderItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderItemOptionSelectionDTO)) {
            return false;
        }

        OrderItemOptionSelectionDTO orderItemOptionSelectionDTO = (OrderItemOptionSelectionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderItemOptionSelectionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItemOptionSelectionDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", optionValue=" + getOptionValue() +
            ", orderItem=" + getOrderItem() +
            "}";
    }
}
