package md.utm.restaurant.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import md.utm.restaurant.domain.enumeration.AllergenType;

/**
 * A DTO for the {@link md.utm.restaurant.domain.MenuItemAllergen} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MenuItemAllergenDTO implements Serializable {

    private Long id;

    @NotNull
    private AllergenType allergen;

    @Size(max = 255)
    private String notes;

    @NotNull
    private MenuItemDTO menuItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AllergenType getAllergen() {
        return allergen;
    }

    public void setAllergen(AllergenType allergen) {
        this.allergen = allergen;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuItemAllergenDTO)) {
            return false;
        }

        MenuItemAllergenDTO menuItemAllergenDTO = (MenuItemAllergenDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, menuItemAllergenDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuItemAllergenDTO{" +
            "id=" + getId() +
            ", allergen='" + getAllergen() + "'" +
            ", notes='" + getNotes() + "'" +
            ", menuItem=" + getMenuItem() +
            "}";
    }
}
