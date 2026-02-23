package md.utm.restaurant.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DiningRoomCriteriaTest {

    @Test
    void newDiningRoomCriteriaHasAllFiltersNullTest() {
        var diningRoomCriteria = new DiningRoomCriteria();
        assertThat(diningRoomCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void diningRoomCriteriaFluentMethodsCreatesFiltersTest() {
        var diningRoomCriteria = new DiningRoomCriteria();

        setAllFilters(diningRoomCriteria);

        assertThat(diningRoomCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void diningRoomCriteriaCopyCreatesNullFilterTest() {
        var diningRoomCriteria = new DiningRoomCriteria();
        var copy = diningRoomCriteria.copy();

        assertThat(diningRoomCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(diningRoomCriteria)
        );
    }

    @Test
    void diningRoomCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var diningRoomCriteria = new DiningRoomCriteria();
        setAllFilters(diningRoomCriteria);

        var copy = diningRoomCriteria.copy();

        assertThat(diningRoomCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(diningRoomCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var diningRoomCriteria = new DiningRoomCriteria();

        assertThat(diningRoomCriteria).hasToString("DiningRoomCriteria{}");
    }

    private static void setAllFilters(DiningRoomCriteria diningRoomCriteria) {
        diningRoomCriteria.id();
        diningRoomCriteria.name();
        diningRoomCriteria.description();
        diningRoomCriteria.floor();
        diningRoomCriteria.capacity();
        diningRoomCriteria.isActive();
        diningRoomCriteria.floorPlanUrl();
        diningRoomCriteria.widthPx();
        diningRoomCriteria.heightPx();
        diningRoomCriteria.tablesId();
        diningRoomCriteria.locationId();
        diningRoomCriteria.distinct();
    }

    private static Condition<DiningRoomCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getFloor()) &&
                condition.apply(criteria.getCapacity()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getFloorPlanUrl()) &&
                condition.apply(criteria.getWidthPx()) &&
                condition.apply(criteria.getHeightPx()) &&
                condition.apply(criteria.getTablesId()) &&
                condition.apply(criteria.getLocationId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DiningRoomCriteria> copyFiltersAre(DiningRoomCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getFloor(), copy.getFloor()) &&
                condition.apply(criteria.getCapacity(), copy.getCapacity()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getFloorPlanUrl(), copy.getFloorPlanUrl()) &&
                condition.apply(criteria.getWidthPx(), copy.getWidthPx()) &&
                condition.apply(criteria.getHeightPx(), copy.getHeightPx()) &&
                condition.apply(criteria.getTablesId(), copy.getTablesId()) &&
                condition.apply(criteria.getLocationId(), copy.getLocationId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
