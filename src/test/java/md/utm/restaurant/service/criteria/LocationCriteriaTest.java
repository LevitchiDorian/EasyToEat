package md.utm.restaurant.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LocationCriteriaTest {

    @Test
    void newLocationCriteriaHasAllFiltersNullTest() {
        var locationCriteria = new LocationCriteria();
        assertThat(locationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void locationCriteriaFluentMethodsCreatesFiltersTest() {
        var locationCriteria = new LocationCriteria();

        setAllFilters(locationCriteria);

        assertThat(locationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void locationCriteriaCopyCreatesNullFilterTest() {
        var locationCriteria = new LocationCriteria();
        var copy = locationCriteria.copy();

        assertThat(locationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(locationCriteria)
        );
    }

    @Test
    void locationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var locationCriteria = new LocationCriteria();
        setAllFilters(locationCriteria);

        var copy = locationCriteria.copy();

        assertThat(locationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(locationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var locationCriteria = new LocationCriteria();

        assertThat(locationCriteria).hasToString("LocationCriteria{}");
    }

    private static void setAllFilters(LocationCriteria locationCriteria) {
        locationCriteria.id();
        locationCriteria.name();
        locationCriteria.address();
        locationCriteria.city();
        locationCriteria.phone();
        locationCriteria.email();
        locationCriteria.latitude();
        locationCriteria.longitude();
        locationCriteria.reservationDurationOverride();
        locationCriteria.maxAdvanceBookingDaysOverride();
        locationCriteria.cancellationDeadlineOverride();
        locationCriteria.isActive();
        locationCriteria.createdAt();
        locationCriteria.hoursId();
        locationCriteria.roomsId();
        locationCriteria.localPromotionsId();
        locationCriteria.menuOverridesId();
        locationCriteria.brandId();
        locationCriteria.distinct();
    }

    private static Condition<LocationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getAddress()) &&
                condition.apply(criteria.getCity()) &&
                condition.apply(criteria.getPhone()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getLatitude()) &&
                condition.apply(criteria.getLongitude()) &&
                condition.apply(criteria.getReservationDurationOverride()) &&
                condition.apply(criteria.getMaxAdvanceBookingDaysOverride()) &&
                condition.apply(criteria.getCancellationDeadlineOverride()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getHoursId()) &&
                condition.apply(criteria.getRoomsId()) &&
                condition.apply(criteria.getLocalPromotionsId()) &&
                condition.apply(criteria.getMenuOverridesId()) &&
                condition.apply(criteria.getBrandId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LocationCriteria> copyFiltersAre(LocationCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getAddress(), copy.getAddress()) &&
                condition.apply(criteria.getCity(), copy.getCity()) &&
                condition.apply(criteria.getPhone(), copy.getPhone()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getLatitude(), copy.getLatitude()) &&
                condition.apply(criteria.getLongitude(), copy.getLongitude()) &&
                condition.apply(criteria.getReservationDurationOverride(), copy.getReservationDurationOverride()) &&
                condition.apply(criteria.getMaxAdvanceBookingDaysOverride(), copy.getMaxAdvanceBookingDaysOverride()) &&
                condition.apply(criteria.getCancellationDeadlineOverride(), copy.getCancellationDeadlineOverride()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getHoursId(), copy.getHoursId()) &&
                condition.apply(criteria.getRoomsId(), copy.getRoomsId()) &&
                condition.apply(criteria.getLocalPromotionsId(), copy.getLocalPromotionsId()) &&
                condition.apply(criteria.getMenuOverridesId(), copy.getMenuOverridesId()) &&
                condition.apply(criteria.getBrandId(), copy.getBrandId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
