package md.utm.restaurant.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ReviewCriteriaTest {

    @Test
    void newReviewCriteriaHasAllFiltersNullTest() {
        var reviewCriteria = new ReviewCriteria();
        assertThat(reviewCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void reviewCriteriaFluentMethodsCreatesFiltersTest() {
        var reviewCriteria = new ReviewCriteria();

        setAllFilters(reviewCriteria);

        assertThat(reviewCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void reviewCriteriaCopyCreatesNullFilterTest() {
        var reviewCriteria = new ReviewCriteria();
        var copy = reviewCriteria.copy();

        assertThat(reviewCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(reviewCriteria)
        );
    }

    @Test
    void reviewCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var reviewCriteria = new ReviewCriteria();
        setAllFilters(reviewCriteria);

        var copy = reviewCriteria.copy();

        assertThat(reviewCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(reviewCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var reviewCriteria = new ReviewCriteria();

        assertThat(reviewCriteria).hasToString("ReviewCriteria{}");
    }

    private static void setAllFilters(ReviewCriteria reviewCriteria) {
        reviewCriteria.id();
        reviewCriteria.overallRating();
        reviewCriteria.foodRating();
        reviewCriteria.serviceRating();
        reviewCriteria.ambienceRating();
        reviewCriteria.isApproved();
        reviewCriteria.isAnonymous();
        reviewCriteria.createdAt();
        reviewCriteria.locationId();
        reviewCriteria.reservationId();
        reviewCriteria.clientId();
        reviewCriteria.distinct();
    }

    private static Condition<ReviewCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getOverallRating()) &&
                condition.apply(criteria.getFoodRating()) &&
                condition.apply(criteria.getServiceRating()) &&
                condition.apply(criteria.getAmbienceRating()) &&
                condition.apply(criteria.getIsApproved()) &&
                condition.apply(criteria.getIsAnonymous()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getLocationId()) &&
                condition.apply(criteria.getReservationId()) &&
                condition.apply(criteria.getClientId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ReviewCriteria> copyFiltersAre(ReviewCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getOverallRating(), copy.getOverallRating()) &&
                condition.apply(criteria.getFoodRating(), copy.getFoodRating()) &&
                condition.apply(criteria.getServiceRating(), copy.getServiceRating()) &&
                condition.apply(criteria.getAmbienceRating(), copy.getAmbienceRating()) &&
                condition.apply(criteria.getIsApproved(), copy.getIsApproved()) &&
                condition.apply(criteria.getIsAnonymous(), copy.getIsAnonymous()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getLocationId(), copy.getLocationId()) &&
                condition.apply(criteria.getReservationId(), copy.getReservationId()) &&
                condition.apply(criteria.getClientId(), copy.getClientId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
