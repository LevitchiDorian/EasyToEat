package md.utm.restaurant.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LocationMenuItemOverrideCriteriaTest {

    @Test
    void newLocationMenuItemOverrideCriteriaHasAllFiltersNullTest() {
        var locationMenuItemOverrideCriteria = new LocationMenuItemOverrideCriteria();
        assertThat(locationMenuItemOverrideCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void locationMenuItemOverrideCriteriaFluentMethodsCreatesFiltersTest() {
        var locationMenuItemOverrideCriteria = new LocationMenuItemOverrideCriteria();

        setAllFilters(locationMenuItemOverrideCriteria);

        assertThat(locationMenuItemOverrideCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void locationMenuItemOverrideCriteriaCopyCreatesNullFilterTest() {
        var locationMenuItemOverrideCriteria = new LocationMenuItemOverrideCriteria();
        var copy = locationMenuItemOverrideCriteria.copy();

        assertThat(locationMenuItemOverrideCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(locationMenuItemOverrideCriteria)
        );
    }

    @Test
    void locationMenuItemOverrideCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var locationMenuItemOverrideCriteria = new LocationMenuItemOverrideCriteria();
        setAllFilters(locationMenuItemOverrideCriteria);

        var copy = locationMenuItemOverrideCriteria.copy();

        assertThat(locationMenuItemOverrideCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(locationMenuItemOverrideCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var locationMenuItemOverrideCriteria = new LocationMenuItemOverrideCriteria();

        assertThat(locationMenuItemOverrideCriteria).hasToString("LocationMenuItemOverrideCriteria{}");
    }

    private static void setAllFilters(LocationMenuItemOverrideCriteria locationMenuItemOverrideCriteria) {
        locationMenuItemOverrideCriteria.id();
        locationMenuItemOverrideCriteria.isAvailableAtLocation();
        locationMenuItemOverrideCriteria.priceOverride();
        locationMenuItemOverrideCriteria.preparationTimeOverride();
        locationMenuItemOverrideCriteria.notes();
        locationMenuItemOverrideCriteria.menuItemId();
        locationMenuItemOverrideCriteria.locationId();
        locationMenuItemOverrideCriteria.distinct();
    }

    private static Condition<LocationMenuItemOverrideCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getIsAvailableAtLocation()) &&
                condition.apply(criteria.getPriceOverride()) &&
                condition.apply(criteria.getPreparationTimeOverride()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getMenuItemId()) &&
                condition.apply(criteria.getLocationId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LocationMenuItemOverrideCriteria> copyFiltersAre(
        LocationMenuItemOverrideCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getIsAvailableAtLocation(), copy.getIsAvailableAtLocation()) &&
                condition.apply(criteria.getPriceOverride(), copy.getPriceOverride()) &&
                condition.apply(criteria.getPreparationTimeOverride(), copy.getPreparationTimeOverride()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getMenuItemId(), copy.getMenuItemId()) &&
                condition.apply(criteria.getLocationId(), copy.getLocationId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
