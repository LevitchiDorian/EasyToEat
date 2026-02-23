package md.utm.restaurant.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link md.utm.restaurant.domain.MenuItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MenuItemDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2, max = 150)
    private String name;

    @Lob
    private String description;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal price;

    @DecimalMin(value = "0")
    private BigDecimal discountedPrice;

    @Min(value = 0)
    @Max(value = 180)
    private Integer preparationTimeMinutes;

    @Min(value = 0)
    private Integer calories;

    @Size(max = 500)
    private String imageUrl;

    @NotNull
    private Boolean isAvailable;

    @NotNull
    private Boolean isFeatured;

    private Boolean isVegetarian;

    private Boolean isVegan;

    private Boolean isGlutenFree;

    @Min(value = 0)
    @Max(value = 3)
    private Integer spicyLevel;

    @NotNull
    @Min(value = 0)
    private Integer displayOrder;

    @NotNull
    private MenuCategoryDTO category;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(BigDecimal discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public Integer getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    public void setPreparationTimeMinutes(Integer preparationTimeMinutes) {
        this.preparationTimeMinutes = preparationTimeMinutes;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Boolean getIsVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(Boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public Boolean getIsVegan() {
        return isVegan;
    }

    public void setIsVegan(Boolean isVegan) {
        this.isVegan = isVegan;
    }

    public Boolean getIsGlutenFree() {
        return isGlutenFree;
    }

    public void setIsGlutenFree(Boolean isGlutenFree) {
        this.isGlutenFree = isGlutenFree;
    }

    public Integer getSpicyLevel() {
        return spicyLevel;
    }

    public void setSpicyLevel(Integer spicyLevel) {
        this.spicyLevel = spicyLevel;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public MenuCategoryDTO getCategory() {
        return category;
    }

    public void setCategory(MenuCategoryDTO category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuItemDTO)) {
            return false;
        }

        MenuItemDTO menuItemDTO = (MenuItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, menuItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuItemDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", discountedPrice=" + getDiscountedPrice() +
            ", preparationTimeMinutes=" + getPreparationTimeMinutes() +
            ", calories=" + getCalories() +
            ", imageUrl='" + getImageUrl() + "'" +
            ", isAvailable='" + getIsAvailable() + "'" +
            ", isFeatured='" + getIsFeatured() + "'" +
            ", isVegetarian='" + getIsVegetarian() + "'" +
            ", isVegan='" + getIsVegan() + "'" +
            ", isGlutenFree='" + getIsGlutenFree() + "'" +
            ", spicyLevel=" + getSpicyLevel() +
            ", displayOrder=" + getDisplayOrder() +
            ", category=" + getCategory() +
            "}";
    }
}
