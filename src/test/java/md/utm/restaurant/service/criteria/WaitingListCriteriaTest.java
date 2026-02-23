package md.utm.restaurant.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WaitingListCriteriaTest {

    @Test
    void newWaitingListCriteriaHasAllFiltersNullTest() {
        var waitingListCriteria = new WaitingListCriteria();
        assertThat(waitingListCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void waitingListCriteriaFluentMethodsCreatesFiltersTest() {
        var waitingListCriteria = new WaitingListCriteria();

        setAllFilters(waitingListCriteria);

        assertThat(waitingListCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void waitingListCriteriaCopyCreatesNullFilterTest() {
        var waitingListCriteria = new WaitingListCriteria();
        var copy = waitingListCriteria.copy();

        assertThat(waitingListCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(waitingListCriteria)
        );
    }

    @Test
    void waitingListCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var waitingListCriteria = new WaitingListCriteria();
        setAllFilters(waitingListCriteria);

        var copy = waitingListCriteria.copy();

        assertThat(waitingListCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(waitingListCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var waitingListCriteria = new WaitingListCriteria();

        assertThat(waitingListCriteria).hasToString("WaitingListCriteria{}");
    }

    private static void setAllFilters(WaitingListCriteria waitingListCriteria) {
        waitingListCriteria.id();
        waitingListCriteria.requestedDate();
        waitingListCriteria.requestedTime();
        waitingListCriteria.partySize();
        waitingListCriteria.notes();
        waitingListCriteria.isNotified();
        waitingListCriteria.expiresAt();
        waitingListCriteria.createdAt();
        waitingListCriteria.locationId();
        waitingListCriteria.clientId();
        waitingListCriteria.roomId();
        waitingListCriteria.distinct();
    }

    private static Condition<WaitingListCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getRequestedDate()) &&
                condition.apply(criteria.getRequestedTime()) &&
                condition.apply(criteria.getPartySize()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getIsNotified()) &&
                condition.apply(criteria.getExpiresAt()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getLocationId()) &&
                condition.apply(criteria.getClientId()) &&
                condition.apply(criteria.getRoomId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WaitingListCriteria> copyFiltersAre(WaitingListCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getRequestedDate(), copy.getRequestedDate()) &&
                condition.apply(criteria.getRequestedTime(), copy.getRequestedTime()) &&
                condition.apply(criteria.getPartySize(), copy.getPartySize()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getIsNotified(), copy.getIsNotified()) &&
                condition.apply(criteria.getExpiresAt(), copy.getExpiresAt()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getLocationId(), copy.getLocationId()) &&
                condition.apply(criteria.getClientId(), copy.getClientId()) &&
                condition.apply(criteria.getRoomId(), copy.getRoomId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
