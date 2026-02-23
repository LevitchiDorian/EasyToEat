package md.utm.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A MenuItem.
 */
@Entity
@Table(name = "menu_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MenuItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2, max = 150)
    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @DecimalMin(value = "0")
    @Column(name = "discounted_price", precision = 21, scale = 2)
    private BigDecimal discountedPrice;

    @Min(value = 0)
    @Max(value = 180)
    @Column(name = "preparation_time_minutes")
    private Integer preparationTimeMinutes;

    @Min(value = 0)
    @Column(name = "calories")
    private Integer calories;

    @Size(max = 500)
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @NotNull
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured;

    @Column(name = "is_vegetarian")
    private Boolean isVegetarian;

    @Column(name = "is_vegan")
    private Boolean isVegan;

    @Column(name = "is_gluten_free")
    private Boolean isGlutenFree;

    @Min(value = 0)
    @Max(value = 3)
    @Column(name = "spicy_level")
    private Integer spicyLevel;

    @NotNull
    @Min(value = 0)
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "menuItem")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "menuItem" }, allowSetters = true)
    private Set<MenuItemAllergen> allergens = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "menuItem")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "values", "menuItem" }, allowSetters = true)
    private Set<MenuItemOption> options = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "items", "parent", "brand" }, allowSetters = true)
    private MenuCategory category;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MenuItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public MenuItem name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public MenuItem description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public MenuItem price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountedPrice() {
        return this.discountedPrice;
    }

    public MenuItem discountedPrice(BigDecimal discountedPrice) {
        this.setDiscountedPrice(discountedPrice);
        return this;
    }

    public void setDiscountedPrice(BigDecimal discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public Integer getPreparationTimeMinutes() {
        return this.preparationTimeMinutes;
    }

    public MenuItem preparationTimeMinutes(Integer preparationTimeMinutes) {
        this.setPreparationTimeMinutes(preparationTimeMinutes);
        return this;
    }

    public void setPreparationTimeMinutes(Integer preparationTimeMinutes) {
        this.preparationTimeMinutes = preparationTimeMinutes;
    }

    public Integer getCalories() {
        return this.calories;
    }

    public MenuItem calories(Integer calories) {
        this.setCalories(calories);
        return this;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public MenuItem imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsAvailable() {
        return this.isAvailable;
    }

    public MenuItem isAvailable(Boolean isAvailable) {
        this.setIsAvailable(isAvailable);
        return this;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Boolean getIsFeatured() {
        return this.isFeatured;
    }

    public MenuItem isFeatured(Boolean isFeatured) {
        this.setIsFeatured(isFeatured);
        return this;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Boolean getIsVegetarian() {
        return this.isVegetarian;
    }

    public MenuItem isVegetarian(Boolean isVegetarian) {
        this.setIsVegetarian(isVegetarian);
        return this;
    }

    public void setIsVegetarian(Boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public Boolean getIsVegan() {
        return this.isVegan;
    }

    public MenuItem isVegan(Boolean isVegan) {
        this.setIsVegan(isVegan);
        return this;
    }

    public void setIsVegan(Boolean isVegan) {
        this.isVegan = isVegan;
    }

    public Boolean getIsGlutenFree() {
        return this.isGlutenFree;
    }

    public MenuItem isGlutenFree(Boolean isGlutenFree) {
        this.setIsGlutenFree(isGlutenFree);
        return this;
    }

    public void setIsGlutenFree(Boolean isGlutenFree) {
        this.isGlutenFree = isGlutenFree;
    }

    public Integer getSpicyLevel() {
        return this.spicyLevel;
    }

    public MenuItem spicyLevel(Integer spicyLevel) {
        this.setSpicyLevel(spicyLevel);
        return this;
    }

    public void setSpicyLevel(Integer spicyLevel) {
        this.spicyLevel = spicyLevel;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public MenuItem displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Set<MenuItemAllergen> getAllergens() {
        return this.allergens;
    }

    public void setAllergens(Set<MenuItemAllergen> menuItemAllergens) {
        if (this.allergens != null) {
            this.allergens.forEach(i -> i.setMenuItem(null));
        }
        if (menuItemAllergens != null) {
            menuItemAllergens.forEach(i -> i.setMenuItem(this));
        }
        this.allergens = menuItemAllergens;
    }

    public MenuItem allergens(Set<MenuItemAllergen> menuItemAllergens) {
        this.setAllergens(menuItemAllergens);
        return this;
    }

    public MenuItem addAllergens(MenuItemAllergen menuItemAllergen) {
        this.allergens.add(menuItemAllergen);
        menuItemAllergen.setMenuItem(this);
        return this;
    }

    public MenuItem removeAllergens(MenuItemAllergen menuItemAllergen) {
        this.allergens.remove(menuItemAllergen);
        menuItemAllergen.setMenuItem(null);
        return this;
    }

    public Set<MenuItemOption> getOptions() {
        return this.options;
    }

    public void setOptions(Set<MenuItemOption> menuItemOptions) {
        if (this.options != null) {
            this.options.forEach(i -> i.setMenuItem(null));
        }
        if (menuItemOptions != null) {
            menuItemOptions.forEach(i -> i.setMenuItem(this));
        }
        this.options = menuItemOptions;
    }

    public MenuItem options(Set<MenuItemOption> menuItemOptions) {
        this.setOptions(menuItemOptions);
        return this;
    }

    public MenuItem addOptions(MenuItemOption menuItemOption) {
        this.options.add(menuItemOption);
        menuItemOption.setMenuItem(this);
        return this;
    }

    public MenuItem removeOptions(MenuItemOption menuItemOption) {
        this.options.remove(menuItemOption);
        menuItemOption.setMenuItem(null);
        return this;
    }

    public MenuCategory getCategory() {
        return this.category;
    }

    public void setCategory(MenuCategory menuCategory) {
        this.category = menuCategory;
    }

    public MenuItem category(MenuCategory menuCategory) {
        this.setCategory(menuCategory);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuItem)) {
            return false;
        }
        return getId() != null && getId().equals(((MenuItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuItem{" +
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
            "}";
    }
}
