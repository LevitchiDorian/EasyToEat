package md.utm.restaurant.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class MenuItemCriteriaTest {

    @Test
    void newMenuItemCriteriaHasAllFiltersNullTest() {
        var menuItemCriteria = new MenuItemCriteria();
        assertThat(menuItemCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void menuItemCriteriaFluentMethodsCreatesFiltersTest() {
        var menuItemCriteria = new MenuItemCriteria();

        setAllFilters(menuItemCriteria);

        assertThat(menuItemCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void menuItemCriteriaCopyCreatesNullFilterTest() {
        var menuItemCriteria = new MenuItemCriteria();
        var copy = menuItemCriteria.copy();

        assertThat(menuItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(menuItemCriteria)
        );
    }

    @Test
    void menuItemCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var menuItemCriteria = new MenuItemCriteria();
        setAllFilters(menuItemCriteria);

        var copy = menuItemCriteria.copy();

        assertThat(menuItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(menuItemCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var menuItemCriteria = new MenuItemCriteria();

        assertThat(menuItemCriteria).hasToString("MenuItemCriteria{}");
    }

    private static void setAllFilters(MenuItemCriteria menuItemCriteria) {
        menuItemCriteria.id();
        menuItemCriteria.name();
        menuItemCriteria.price();
        menuItemCriteria.discountedPrice();
        menuItemCriteria.preparationTimeMinutes();
        menuItemCriteria.calories();
        menuItemCriteria.imageUrl();
        menuItemCriteria.isAvailable();
        menuItemCriteria.isFeatured();
        menuItemCriteria.isVegetarian();
        menuItemCriteria.isVegan();
        menuItemCriteria.isGlutenFree();
        menuItemCriteria.spicyLevel();
        menuItemCriteria.displayOrder();
        menuItemCriteria.allergensId();
        menuItemCriteria.optionsId();
        menuItemCriteria.categoryId();
        menuItemCriteria.distinct();
    }

    private static Condition<MenuItemCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getPrice()) &&
                condition.apply(criteria.getDiscountedPrice()) &&
                condition.apply(criteria.getPreparationTimeMinutes()) &&
                condition.apply(criteria.getCalories()) &&
                condition.apply(criteria.getImageUrl()) &&
                condition.apply(criteria.getIsAvailable()) &&
                condition.apply(criteria.getIsFeatured()) &&
                condition.apply(criteria.getIsVegetarian()) &&
                condition.apply(criteria.getIsVegan()) &&
                condition.apply(criteria.getIsGlutenFree()) &&
                condition.apply(criteria.getSpicyLevel()) &&
                condition.apply(criteria.getDisplayOrder()) &&
                condition.apply(criteria.getAllergensId()) &&
                condition.apply(criteria.getOptionsId()) &&
                condition.apply(criteria.getCategoryId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<MenuItemCriteria> copyFiltersAre(MenuItemCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getPrice(), copy.getPrice()) &&
                condition.apply(criteria.getDiscountedPrice(), copy.getDiscountedPrice()) &&
                condition.apply(criteria.getPreparationTimeMinutes(), copy.getPreparationTimeMinutes()) &&
                condition.apply(criteria.getCalories(), copy.getCalories()) &&
                condition.apply(criteria.getImageUrl(), copy.getImageUrl()) &&
                condition.apply(criteria.getIsAvailable(), copy.getIsAvailable()) &&
                condition.apply(criteria.getIsFeatured(), copy.getIsFeatured()) &&
                condition.apply(criteria.getIsVegetarian(), copy.getIsVegetarian()) &&
                condition.apply(criteria.getIsVegan(), copy.getIsVegan()) &&
                condition.apply(criteria.getIsGlutenFree(), copy.getIsGlutenFree()) &&
                condition.apply(criteria.getSpicyLevel(), copy.getSpicyLevel()) &&
                condition.apply(criteria.getDisplayOrder(), copy.getDisplayOrder()) &&
                condition.apply(criteria.getAllergensId(), copy.getAllergensId()) &&
                condition.apply(criteria.getOptionsId(), copy.getOptionsId()) &&
                condition.apply(criteria.getCategoryId(), copy.getCategoryId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
