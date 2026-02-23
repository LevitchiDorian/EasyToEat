package md.utm.restaurant.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ReservationCriteriaTest {

    @Test
    void newReservationCriteriaHasAllFiltersNullTest() {
        var reservationCriteria = new ReservationCriteria();
        assertThat(reservationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void reservationCriteriaFluentMethodsCreatesFiltersTest() {
        var reservationCriteria = new ReservationCriteria();

        setAllFilters(reservationCriteria);

        assertThat(reservationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void reservationCriteriaCopyCreatesNullFilterTest() {
        var reservationCriteria = new ReservationCriteria();
        var copy = reservationCriteria.copy();

        assertThat(reservationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(reservationCriteria)
        );
    }

    @Test
    void reservationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var reservationCriteria = new ReservationCriteria();
        setAllFilters(reservationCriteria);

        var copy = reservationCriteria.copy();

        assertThat(reservationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(reservationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var reservationCriteria = new ReservationCriteria();

        assertThat(reservationCriteria).hasToString("ReservationCriteria{}");
    }

    private static void setAllFilters(ReservationCriteria reservationCriteria) {
        reservationCriteria.id();
        reservationCriteria.reservationCode();
        reservationCriteria.reservationDate();
        reservationCriteria.startTime();
        reservationCriteria.endTime();
        reservationCriteria.partySize();
        reservationCriteria.status();
        reservationCriteria.reminderSentAt();
        reservationCriteria.confirmedAt();
        reservationCriteria.cancelledAt();
        reservationCriteria.cancellationReason();
        reservationCriteria.createdAt();
        reservationCriteria.updatedAt();
        reservationCriteria.tableAssignmentsId();
        reservationCriteria.ordersId();
        reservationCriteria.locationId();
        reservationCriteria.clientId();
        reservationCriteria.roomId();
        reservationCriteria.distinct();
    }

    private static Condition<ReservationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReservationCode()) &&
                condition.apply(criteria.getReservationDate()) &&
                condition.apply(criteria.getStartTime()) &&
                condition.apply(criteria.getEndTime()) &&
                condition.apply(criteria.getPartySize()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getReminderSentAt()) &&
                condition.apply(criteria.getConfirmedAt()) &&
                condition.apply(criteria.getCancelledAt()) &&
                condition.apply(criteria.getCancellationReason()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getTableAssignmentsId()) &&
                condition.apply(criteria.getOrdersId()) &&
                condition.apply(criteria.getLocationId()) &&
                condition.apply(criteria.getClientId()) &&
                condition.apply(criteria.getRoomId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ReservationCriteria> copyFiltersAre(ReservationCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReservationCode(), copy.getReservationCode()) &&
                condition.apply(criteria.getReservationDate(), copy.getReservationDate()) &&
                condition.apply(criteria.getStartTime(), copy.getStartTime()) &&
                condition.apply(criteria.getEndTime(), copy.getEndTime()) &&
                condition.apply(criteria.getPartySize(), copy.getPartySize()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getReminderSentAt(), copy.getReminderSentAt()) &&
                condition.apply(criteria.getConfirmedAt(), copy.getConfirmedAt()) &&
                condition.apply(criteria.getCancelledAt(), copy.getCancelledAt()) &&
                condition.apply(criteria.getCancellationReason(), copy.getCancellationReason()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getTableAssignmentsId(), copy.getTableAssignmentsId()) &&
                condition.apply(criteria.getOrdersId(), copy.getOrdersId()) &&
                condition.apply(criteria.getLocationId(), copy.getLocationId()) &&
                condition.apply(criteria.getClientId(), copy.getClientId()) &&
                condition.apply(criteria.getRoomId(), copy.getRoomId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
