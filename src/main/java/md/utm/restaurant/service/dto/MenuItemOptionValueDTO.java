package md.utm.restaurant.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link md.utm.restaurant.domain.MenuItemOptionValue} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MenuItemOptionValueDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String label;

    @NotNull
    private BigDecimal priceAdjustment;

    @NotNull
    private Boolean isDefault;

    @NotNull
    private Boolean isAvailable;

    @Min(value = 0)
    private Integer displayOrder;

    @NotNull
    private MenuItemOptionDTO option;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getPriceAdjustment() {
        return priceAdjustment;
    }

    public void setPriceAdjustment(BigDecimal priceAdjustment) {
        this.priceAdjustment = priceAdjustment;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public MenuItemOptionDTO getOption() {
        return option;
    }

    public void setOption(MenuItemOptionDTO option) {
        this.option = option;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuItemOptionValueDTO)) {
            return false;
        }

        MenuItemOptionValueDTO menuItemOptionValueDTO = (MenuItemOptionValueDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, menuItemOptionValueDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuItemOptionValueDTO{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", priceAdjustment=" + getPriceAdjustment() +
            ", isDefault='" + getIsDefault() + "'" +
            ", isAvailable='" + getIsAvailable() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", option=" + getOption() +
            "}";
    }
}
