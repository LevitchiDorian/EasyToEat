package md.utm.restaurant.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link md.utm.restaurant.domain.MenuItem} entity. This class is used
 * in {@link md.utm.restaurant.web.rest.MenuItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /menu-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MenuItemCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private BigDecimalFilter price;

    private BigDecimalFilter discountedPrice;

    private IntegerFilter preparationTimeMinutes;

    private IntegerFilter calories;

    private StringFilter imageUrl;

    private BooleanFilter isAvailable;

    private BooleanFilter isFeatured;

    private BooleanFilter isVegetarian;

    private BooleanFilter isVegan;

    private BooleanFilter isGlutenFree;

    private IntegerFilter spicyLevel;

    private IntegerFilter displayOrder;

    private LongFilter allergensId;

    private LongFilter optionsId;

    private LongFilter categoryId;

    private Boolean distinct;

    public MenuItemCriteria() {}

    public MenuItemCriteria(MenuItemCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.price = other.optionalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.discountedPrice = other.optionalDiscountedPrice().map(BigDecimalFilter::copy).orElse(null);
        this.preparationTimeMinutes = other.optionalPreparationTimeMinutes().map(IntegerFilter::copy).orElse(null);
        this.calories = other.optionalCalories().map(IntegerFilter::copy).orElse(null);
        this.imageUrl = other.optionalImageUrl().map(StringFilter::copy).orElse(null);
        this.isAvailable = other.optionalIsAvailable().map(BooleanFilter::copy).orElse(null);
        this.isFeatured = other.optionalIsFeatured().map(BooleanFilter::copy).orElse(null);
        this.isVegetarian = other.optionalIsVegetarian().map(BooleanFilter::copy).orElse(null);
        this.isVegan = other.optionalIsVegan().map(BooleanFilter::copy).orElse(null);
        this.isGlutenFree = other.optionalIsGlutenFree().map(BooleanFilter::copy).orElse(null);
        this.spicyLevel = other.optionalSpicyLevel().map(IntegerFilter::copy).orElse(null);
        this.displayOrder = other.optionalDisplayOrder().map(IntegerFilter::copy).orElse(null);
        this.allergensId = other.optionalAllergensId().map(LongFilter::copy).orElse(null);
        this.optionsId = other.optionalOptionsId().map(LongFilter::copy).orElse(null);
        this.categoryId = other.optionalCategoryId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public MenuItemCriteria copy() {
        return new MenuItemCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public BigDecimalFilter getPrice() {
        return price;
    }

    public Optional<BigDecimalFilter> optionalPrice() {
        return Optional.ofNullable(price);
    }

    public BigDecimalFilter price() {
        if (price == null) {
            setPrice(new BigDecimalFilter());
        }
        return price;
    }

    public void setPrice(BigDecimalFilter price) {
        this.price = price;
    }

    public BigDecimalFilter getDiscountedPrice() {
        return discountedPrice;
    }

    public Optional<BigDecimalFilter> optionalDiscountedPrice() {
        return Optional.ofNullable(discountedPrice);
    }

    public BigDecimalFilter discountedPrice() {
        if (discountedPrice == null) {
            setDiscountedPrice(new BigDecimalFilter());
        }
        return discountedPrice;
    }

    public void setDiscountedPrice(BigDecimalFilter discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public IntegerFilter getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    public Optional<IntegerFilter> optionalPreparationTimeMinutes() {
        return Optional.ofNullable(preparationTimeMinutes);
    }

    public IntegerFilter preparationTimeMinutes() {
        if (preparationTimeMinutes == null) {
            setPreparationTimeMinutes(new IntegerFilter());
        }
        return preparationTimeMinutes;
    }

    public void setPreparationTimeMinutes(IntegerFilter preparationTimeMinutes) {
        this.preparationTimeMinutes = preparationTimeMinutes;
    }

    public IntegerFilter getCalories() {
        return calories;
    }

    public Optional<IntegerFilter> optionalCalories() {
        return Optional.ofNullable(calories);
    }

    public IntegerFilter calories() {
        if (calories == null) {
            setCalories(new IntegerFilter());
        }
        return calories;
    }

    public void setCalories(IntegerFilter calories) {
        this.calories = calories;
    }

    public StringFilter getImageUrl() {
        return imageUrl;
    }

    public Optional<StringFilter> optionalImageUrl() {
        return Optional.ofNullable(imageUrl);
    }

    public StringFilter imageUrl() {
        if (imageUrl == null) {
            setImageUrl(new StringFilter());
        }
        return imageUrl;
    }

    public void setImageUrl(StringFilter imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BooleanFilter getIsAvailable() {
        return isAvailable;
    }

    public Optional<BooleanFilter> optionalIsAvailable() {
        return Optional.ofNullable(isAvailable);
    }

    public BooleanFilter isAvailable() {
        if (isAvailable == null) {
            setIsAvailable(new BooleanFilter());
        }
        return isAvailable;
    }

    public void setIsAvailable(BooleanFilter isAvailable) {
        this.isAvailable = isAvailable;
    }

    public BooleanFilter getIsFeatured() {
        return isFeatured;
    }

    public Optional<BooleanFilter> optionalIsFeatured() {
        return Optional.ofNullable(isFeatured);
    }

    public BooleanFilter isFeatured() {
        if (isFeatured == null) {
            setIsFeatured(new BooleanFilter());
        }
        return isFeatured;
    }

    public void setIsFeatured(BooleanFilter isFeatured) {
        this.isFeatured = isFeatured;
    }

    public BooleanFilter getIsVegetarian() {
        return isVegetarian;
    }

    public Optional<BooleanFilter> optionalIsVegetarian() {
        return Optional.ofNullable(isVegetarian);
    }

    public BooleanFilter isVegetarian() {
        if (isVegetarian == null) {
            setIsVegetarian(new BooleanFilter());
        }
        return isVegetarian;
    }

    public void setIsVegetarian(BooleanFilter isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public BooleanFilter getIsVegan() {
        return isVegan;
    }

    public Optional<BooleanFilter> optionalIsVegan() {
        return Optional.ofNullable(isVegan);
    }

    public BooleanFilter isVegan() {
        if (isVegan == null) {
            setIsVegan(new BooleanFilter());
        }
        return isVegan;
    }

    public void setIsVegan(BooleanFilter isVegan) {
        this.isVegan = isVegan;
    }

    public BooleanFilter getIsGlutenFree() {
        return isGlutenFree;
    }

    public Optional<BooleanFilter> optionalIsGlutenFree() {
        return Optional.ofNullable(isGlutenFree);
    }

    public BooleanFilter isGlutenFree() {
        if (isGlutenFree == null) {
            setIsGlutenFree(new BooleanFilter());
        }
        return isGlutenFree;
    }

    public void setIsGlutenFree(BooleanFilter isGlutenFree) {
        this.isGlutenFree = isGlutenFree;
    }

    public IntegerFilter getSpicyLevel() {
        return spicyLevel;
    }

    public Optional<IntegerFilter> optionalSpicyLevel() {
        return Optional.ofNullable(spicyLevel);
    }

    public IntegerFilter spicyLevel() {
        if (spicyLevel == null) {
            setSpicyLevel(new IntegerFilter());
        }
        return spicyLevel;
    }

    public void setSpicyLevel(IntegerFilter spicyLevel) {
        this.spicyLevel = spicyLevel;
    }

    public IntegerFilter getDisplayOrder() {
        return displayOrder;
    }

    public Optional<IntegerFilter> optionalDisplayOrder() {
        return Optional.ofNullable(displayOrder);
    }

    public IntegerFilter displayOrder() {
        if (displayOrder == null) {
            setDisplayOrder(new IntegerFilter());
        }
        return displayOrder;
    }

    public void setDisplayOrder(IntegerFilter displayOrder) {
        this.displayOrder = displayOrder;
    }

    public LongFilter getAllergensId() {
        return allergensId;
    }

    public Optional<LongFilter> optionalAllergensId() {
        return Optional.ofNullable(allergensId);
    }

    public LongFilter allergensId() {
        if (allergensId == null) {
            setAllergensId(new LongFilter());
        }
        return allergensId;
    }

    public void setAllergensId(LongFilter allergensId) {
        this.allergensId = allergensId;
    }

    public LongFilter getOptionsId() {
        return optionsId;
    }

    public Optional<LongFilter> optionalOptionsId() {
        return Optional.ofNullable(optionsId);
    }

    public LongFilter optionsId() {
        if (optionsId == null) {
            setOptionsId(new LongFilter());
        }
        return optionsId;
    }

    public void setOptionsId(LongFilter optionsId) {
        this.optionsId = optionsId;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public Optional<LongFilter> optionalCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            setCategoryId(new LongFilter());
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MenuItemCriteria that = (MenuItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(price, that.price) &&
            Objects.equals(discountedPrice, that.discountedPrice) &&
            Objects.equals(preparationTimeMinutes, that.preparationTimeMinutes) &&
            Objects.equals(calories, that.calories) &&
            Objects.equals(imageUrl, that.imageUrl) &&
            Objects.equals(isAvailable, that.isAvailable) &&
            Objects.equals(isFeatured, that.isFeatured) &&
            Objects.equals(isVegetarian, that.isVegetarian) &&
            Objects.equals(isVegan, that.isVegan) &&
            Objects.equals(isGlutenFree, that.isGlutenFree) &&
            Objects.equals(spicyLevel, that.spicyLevel) &&
            Objects.equals(displayOrder, that.displayOrder) &&
            Objects.equals(allergensId, that.allergensId) &&
            Objects.equals(optionsId, that.optionsId) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            price,
            discountedPrice,
            preparationTimeMinutes,
            calories,
            imageUrl,
            isAvailable,
            isFeatured,
            isVegetarian,
            isVegan,
            isGlutenFree,
            spicyLevel,
            displayOrder,
            allergensId,
            optionsId,
            categoryId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuItemCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalPrice().map(f -> "price=" + f + ", ").orElse("") +
            optionalDiscountedPrice().map(f -> "discountedPrice=" + f + ", ").orElse("") +
            optionalPreparationTimeMinutes().map(f -> "preparationTimeMinutes=" + f + ", ").orElse("") +
            optionalCalories().map(f -> "calories=" + f + ", ").orElse("") +
            optionalImageUrl().map(f -> "imageUrl=" + f + ", ").orElse("") +
            optionalIsAvailable().map(f -> "isAvailable=" + f + ", ").orElse("") +
            optionalIsFeatured().map(f -> "isFeatured=" + f + ", ").orElse("") +
            optionalIsVegetarian().map(f -> "isVegetarian=" + f + ", ").orElse("") +
            optionalIsVegan().map(f -> "isVegan=" + f + ", ").orElse("") +
            optionalIsGlutenFree().map(f -> "isGlutenFree=" + f + ", ").orElse("") +
            optionalSpicyLevel().map(f -> "spicyLevel=" + f + ", ").orElse("") +
            optionalDisplayOrder().map(f -> "displayOrder=" + f + ", ").orElse("") +
            optionalAllergensId().map(f -> "allergensId=" + f + ", ").orElse("") +
            optionalOptionsId().map(f -> "optionsId=" + f + ", ").orElse("") +
            optionalCategoryId().map(f -> "categoryId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
