package md.utm.restaurant.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RestaurantOrderCriteriaTest {

    @Test
    void newRestaurantOrderCriteriaHasAllFiltersNullTest() {
        var restaurantOrderCriteria = new RestaurantOrderCriteria();
        assertThat(restaurantOrderCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void restaurantOrderCriteriaFluentMethodsCreatesFiltersTest() {
        var restaurantOrderCriteria = new RestaurantOrderCriteria();

        setAllFilters(restaurantOrderCriteria);

        assertThat(restaurantOrderCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void restaurantOrderCriteriaCopyCreatesNullFilterTest() {
        var restaurantOrderCriteria = new RestaurantOrderCriteria();
        var copy = restaurantOrderCriteria.copy();

        assertThat(restaurantOrderCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(restaurantOrderCriteria)
        );
    }

    @Test
    void restaurantOrderCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var restaurantOrderCriteria = new RestaurantOrderCriteria();
        setAllFilters(restaurantOrderCriteria);

        var copy = restaurantOrderCriteria.copy();

        assertThat(restaurantOrderCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(restaurantOrderCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var restaurantOrderCriteria = new RestaurantOrderCriteria();

        assertThat(restaurantOrderCriteria).hasToString("RestaurantOrderCriteria{}");
    }

    private static void setAllFilters(RestaurantOrderCriteria restaurantOrderCriteria) {
        restaurantOrderCriteria.id();
        restaurantOrderCriteria.orderCode();
        restaurantOrderCriteria.status();
        restaurantOrderCriteria.isPreOrder();
        restaurantOrderCriteria.scheduledFor();
        restaurantOrderCriteria.subtotal();
        restaurantOrderCriteria.discountAmount();
        restaurantOrderCriteria.taxAmount();
        restaurantOrderCriteria.totalAmount();
        restaurantOrderCriteria.estimatedReadyTime();
        restaurantOrderCriteria.confirmedAt();
        restaurantOrderCriteria.completedAt();
        restaurantOrderCriteria.createdAt();
        restaurantOrderCriteria.updatedAt();
        restaurantOrderCriteria.itemsId();
        restaurantOrderCriteria.paymentsId();
        restaurantOrderCriteria.locationId();
        restaurantOrderCriteria.clientId();
        restaurantOrderCriteria.assignedWaiterId();
        restaurantOrderCriteria.tableId();
        restaurantOrderCriteria.promotionId();
        restaurantOrderCriteria.reservationId();
        restaurantOrderCriteria.distinct();
    }

    private static Condition<RestaurantOrderCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getOrderCode()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getIsPreOrder()) &&
                condition.apply(criteria.getScheduledFor()) &&
                condition.apply(criteria.getSubtotal()) &&
                condition.apply(criteria.getDiscountAmount()) &&
                condition.apply(criteria.getTaxAmount()) &&
                condition.apply(criteria.getTotalAmount()) &&
                condition.apply(criteria.getEstimatedReadyTime()) &&
                condition.apply(criteria.getConfirmedAt()) &&
                condition.apply(criteria.getCompletedAt()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getItemsId()) &&
                condition.apply(criteria.getPaymentsId()) &&
                condition.apply(criteria.getLocationId()) &&
                condition.apply(criteria.getClientId()) &&
                condition.apply(criteria.getAssignedWaiterId()) &&
                condition.apply(criteria.getTableId()) &&
                condition.apply(criteria.getPromotionId()) &&
                condition.apply(criteria.getReservationId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RestaurantOrderCriteria> copyFiltersAre(
        RestaurantOrderCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getOrderCode(), copy.getOrderCode()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getIsPreOrder(), copy.getIsPreOrder()) &&
                condition.apply(criteria.getScheduledFor(), copy.getScheduledFor()) &&
                condition.apply(criteria.getSubtotal(), copy.getSubtotal()) &&
                condition.apply(criteria.getDiscountAmount(), copy.getDiscountAmount()) &&
                condition.apply(criteria.getTaxAmount(), copy.getTaxAmount()) &&
                condition.apply(criteria.getTotalAmount(), copy.getTotalAmount()) &&
                condition.apply(criteria.getEstimatedReadyTime(), copy.getEstimatedReadyTime()) &&
                condition.apply(criteria.getConfirmedAt(), copy.getConfirmedAt()) &&
                condition.apply(criteria.getCompletedAt(), copy.getCompletedAt()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getItemsId(), copy.getItemsId()) &&
                condition.apply(criteria.getPaymentsId(), copy.getPaymentsId()) &&
                condition.apply(criteria.getLocationId(), copy.getLocationId()) &&
                condition.apply(criteria.getClientId(), copy.getClientId()) &&
                condition.apply(criteria.getAssignedWaiterId(), copy.getAssignedWaiterId()) &&
                condition.apply(criteria.getTableId(), copy.getTableId()) &&
                condition.apply(criteria.getPromotionId(), copy.getPromotionId()) &&
                condition.apply(criteria.getReservationId(), copy.getReservationId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
