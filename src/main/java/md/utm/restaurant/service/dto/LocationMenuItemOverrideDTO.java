package md.utm.restaurant.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link md.utm.restaurant.domain.LocationMenuItemOverride} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LocationMenuItemOverrideDTO implements Serializable {

    private Long id;

    @NotNull
    private Boolean isAvailableAtLocation;

    @DecimalMin(value = "0")
    private BigDecimal priceOverride;

    @Min(value = 0)
    @Max(value = 180)
    private Integer preparationTimeOverride;

    @Size(max = 500)
    private String notes;

    private MenuItemDTO menuItem;

    @NotNull
    private LocationDTO location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsAvailableAtLocation() {
        return isAvailableAtLocation;
    }

    public void setIsAvailableAtLocation(Boolean isAvailableAtLocation) {
        this.isAvailableAtLocation = isAvailableAtLocation;
    }

    public BigDecimal getPriceOverride() {
        return priceOverride;
    }

    public void setPriceOverride(BigDecimal priceOverride) {
        this.priceOverride = priceOverride;
    }

    public Integer getPreparationTimeOverride() {
        return preparationTimeOverride;
    }

    public void setPreparationTimeOverride(Integer preparationTimeOverride) {
        this.preparationTimeOverride = preparationTimeOverride;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public MenuItemDTO getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItemDTO menuItem) {
        this.menuItem = menuItem;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocationMenuItemOverrideDTO)) {
            return false;
        }

        LocationMenuItemOverrideDTO locationMenuItemOverrideDTO = (LocationMenuItemOverrideDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, locationMenuItemOverrideDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocationMenuItemOverrideDTO{" +
            "id=" + getId() +
            ", isAvailableAtLocation='" + getIsAvailableAtLocation() + "'" +
            ", priceOverride=" + getPriceOverride() +
            ", preparationTimeOverride=" + getPreparationTimeOverride() +
            ", notes='" + getNotes() + "'" +
            ", menuItem=" + getMenuItem() +
            ", location=" + getLocation() +
            "}";
    }
}
