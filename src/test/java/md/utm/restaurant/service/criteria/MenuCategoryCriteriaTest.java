package md.utm.restaurant.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class MenuCategoryCriteriaTest {

    @Test
    void newMenuCategoryCriteriaHasAllFiltersNullTest() {
        var menuCategoryCriteria = new MenuCategoryCriteria();
        assertThat(menuCategoryCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void menuCategoryCriteriaFluentMethodsCreatesFiltersTest() {
        var menuCategoryCriteria = new MenuCategoryCriteria();

        setAllFilters(menuCategoryCriteria);

        assertThat(menuCategoryCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void menuCategoryCriteriaCopyCreatesNullFilterTest() {
        var menuCategoryCriteria = new MenuCategoryCriteria();
        var copy = menuCategoryCriteria.copy();

        assertThat(menuCategoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(menuCategoryCriteria)
        );
    }

    @Test
    void menuCategoryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var menuCategoryCriteria = new MenuCategoryCriteria();
        setAllFilters(menuCategoryCriteria);

        var copy = menuCategoryCriteria.copy();

        assertThat(menuCategoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(menuCategoryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var menuCategoryCriteria = new MenuCategoryCriteria();

        assertThat(menuCategoryCriteria).hasToString("MenuCategoryCriteria{}");
    }

    private static void setAllFilters(MenuCategoryCriteria menuCategoryCriteria) {
        menuCategoryCriteria.id();
        menuCategoryCriteria.name();
        menuCategoryCriteria.description();
        menuCategoryCriteria.imageUrl();
        menuCategoryCriteria.displayOrder();
        menuCategoryCriteria.isActive();
        menuCategoryCriteria.itemsId();
        menuCategoryCriteria.parentId();
        menuCategoryCriteria.brandId();
        menuCategoryCriteria.distinct();
    }

    private static Condition<MenuCategoryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getImageUrl()) &&
                condition.apply(criteria.getDisplayOrder()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getItemsId()) &&
                condition.apply(criteria.getParentId()) &&
                condition.apply(criteria.getBrandId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<MenuCategoryCriteria> copyFiltersAre(
        MenuCategoryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getImageUrl(), copy.getImageUrl()) &&
                condition.apply(criteria.getDisplayOrder(), copy.getDisplayOrder()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getItemsId(), copy.getItemsId()) &&
                condition.apply(criteria.getParentId(), copy.getParentId()) &&
                condition.apply(criteria.getBrandId(), copy.getBrandId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
