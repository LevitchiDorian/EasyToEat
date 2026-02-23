package md.utm.restaurant.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link md.utm.restaurant.domain.MenuItemOption} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MenuItemOptionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    private Boolean isRequired;

    @Min(value = 1)
    private Integer maxSelections;

    @Min(value = 0)
    private Integer displayOrder;

    @NotNull
    private MenuItemDTO menuItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Integer getMaxSelections() {
        return maxSelections;
    }

    public void setMaxSelections(Integer maxSelections) {
        this.maxSelections = maxSelections;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public MenuItemDTO getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItemDTO menuItem) {
        this.menuItem = menuItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuItemOptionDTO)) {
            return false;
        }

        MenuItemOptionDTO menuItemOptionDTO = (MenuItemOptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, menuItemOptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuItemOptionDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", isRequired='" + getIsRequired() + "'" +
            ", maxSelections=" + getMaxSelections() +
            ", displayOrder=" + getDisplayOrder() +
            ", menuItem=" + getMenuItem() +
            "}";
    }
}
