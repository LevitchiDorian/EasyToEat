package md.utm.restaurant.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RestaurantTableCriteriaTest {

    @Test
    void newRestaurantTableCriteriaHasAllFiltersNullTest() {
        var restaurantTableCriteria = new RestaurantTableCriteria();
        assertThat(restaurantTableCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void restaurantTableCriteriaFluentMethodsCreatesFiltersTest() {
        var restaurantTableCriteria = new RestaurantTableCriteria();

        setAllFilters(restaurantTableCriteria);

        assertThat(restaurantTableCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void restaurantTableCriteriaCopyCreatesNullFilterTest() {
        var restaurantTableCriteria = new RestaurantTableCriteria();
        var copy = restaurantTableCriteria.copy();

        assertThat(restaurantTableCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(restaurantTableCriteria)
        );
    }

    @Test
    void restaurantTableCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var restaurantTableCriteria = new RestaurantTableCriteria();
        setAllFilters(restaurantTableCriteria);

        var copy = restaurantTableCriteria.copy();

        assertThat(restaurantTableCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(restaurantTableCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var restaurantTableCriteria = new RestaurantTableCriteria();

        assertThat(restaurantTableCriteria).hasToString("RestaurantTableCriteria{}");
    }

    private static void setAllFilters(RestaurantTableCriteria restaurantTableCriteria) {
        restaurantTableCriteria.id();
        restaurantTableCriteria.tableNumber();
        restaurantTableCriteria.shape();
        restaurantTableCriteria.minCapacity();
        restaurantTableCriteria.maxCapacity();
        restaurantTableCriteria.positionX();
        restaurantTableCriteria.positionY();
        restaurantTableCriteria.widthPx();
        restaurantTableCriteria.heightPx();
        restaurantTableCriteria.rotation();
        restaurantTableCriteria.status();
        restaurantTableCriteria.isActive();
        restaurantTableCriteria.notes();
        restaurantTableCriteria.roomId();
        restaurantTableCriteria.distinct();
    }

    private static Condition<RestaurantTableCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTableNumber()) &&
                condition.apply(criteria.getShape()) &&
                condition.apply(criteria.getMinCapacity()) &&
                condition.apply(criteria.getMaxCapacity()) &&
                condition.apply(criteria.getPositionX()) &&
                condition.apply(criteria.getPositionY()) &&
                condition.apply(criteria.getWidthPx()) &&
                condition.apply(criteria.getHeightPx()) &&
                condition.apply(criteria.getRotation()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getRoomId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RestaurantTableCriteria> copyFiltersAre(
        RestaurantTableCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTableNumber(), copy.getTableNumber()) &&
                condition.apply(criteria.getShape(), copy.getShape()) &&
                condition.apply(criteria.getMinCapacity(), copy.getMinCapacity()) &&
                condition.apply(criteria.getMaxCapacity(), copy.getMaxCapacity()) &&
                condition.apply(criteria.getPositionX(), copy.getPositionX()) &&
                condition.apply(criteria.getPositionY(), copy.getPositionY()) &&
                condition.apply(criteria.getWidthPx(), copy.getWidthPx()) &&
                condition.apply(criteria.getHeightPx(), copy.getHeightPx()) &&
                condition.apply(criteria.getRotation(), copy.getRotation()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getRoomId(), copy.getRoomId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
