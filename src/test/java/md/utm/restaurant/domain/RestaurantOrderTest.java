package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.LocationTestSamples.*;
import static md.utm.restaurant.domain.OrderItemTestSamples.*;
import static md.utm.restaurant.domain.PaymentTestSamples.*;
import static md.utm.restaurant.domain.PromotionTestSamples.*;
import static md.utm.restaurant.domain.ReservationTestSamples.*;
import static md.utm.restaurant.domain.RestaurantOrderTestSamples.*;
import static md.utm.restaurant.domain.RestaurantTableTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RestaurantOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestaurantOrder.class);
        RestaurantOrder restaurantOrder1 = getRestaurantOrderSample1();
        RestaurantOrder restaurantOrder2 = new RestaurantOrder();
        assertThat(restaurantOrder1).isNotEqualTo(restaurantOrder2);

        restaurantOrder2.setId(restaurantOrder1.getId());
        assertThat(restaurantOrder1).isEqualTo(restaurantOrder2);

        restaurantOrder2 = getRestaurantOrderSample2();
        assertThat(restaurantOrder1).isNotEqualTo(restaurantOrder2);
    }

    @Test
    void itemsTest() {
        RestaurantOrder restaurantOrder = getRestaurantOrderRandomSampleGenerator();
        OrderItem orderItemBack = getOrderItemRandomSampleGenerator();

        restaurantOrder.addItems(orderItemBack);
        assertThat(restaurantOrder.getItems()).containsOnly(orderItemBack);
        assertThat(orderItemBack.getOrder()).isEqualTo(restaurantOrder);

        restaurantOrder.removeItems(orderItemBack);
        assertThat(restaurantOrder.getItems()).doesNotContain(orderItemBack);
        assertThat(orderItemBack.getOrder()).isNull();

        restaurantOrder.items(new HashSet<>(Set.of(orderItemBack)));
        assertThat(restaurantOrder.getItems()).containsOnly(orderItemBack);
        assertThat(orderItemBack.getOrder()).isEqualTo(restaurantOrder);

        restaurantOrder.setItems(new HashSet<>());
        assertThat(restaurantOrder.getItems()).doesNotContain(orderItemBack);
        assertThat(orderItemBack.getOrder()).isNull();
    }

    @Test
    void paymentsTest() {
        RestaurantOrder restaurantOrder = getRestaurantOrderRandomSampleGenerator();
        Payment paymentBack = getPaymentRandomSampleGenerator();

        restaurantOrder.addPayments(paymentBack);
        assertThat(restaurantOrder.getPayments()).containsOnly(paymentBack);
        assertThat(paymentBack.getOrder()).isEqualTo(restaurantOrder);

        restaurantOrder.removePayments(paymentBack);
        assertThat(restaurantOrder.getPayments()).doesNotContain(paymentBack);
        assertThat(paymentBack.getOrder()).isNull();

        restaurantOrder.payments(new HashSet<>(Set.of(paymentBack)));
        assertThat(restaurantOrder.getPayments()).containsOnly(paymentBack);
        assertThat(paymentBack.getOrder()).isEqualTo(restaurantOrder);

        restaurantOrder.setPayments(new HashSet<>());
        assertThat(restaurantOrder.getPayments()).doesNotContain(paymentBack);
        assertThat(paymentBack.getOrder()).isNull();
    }

    @Test
    void locationTest() {
        RestaurantOrder restaurantOrder = getRestaurantOrderRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        restaurantOrder.setLocation(locationBack);
        assertThat(restaurantOrder.getLocation()).isEqualTo(locationBack);

        restaurantOrder.location(null);
        assertThat(restaurantOrder.getLocation()).isNull();
    }

    @Test
    void tableTest() {
        RestaurantOrder restaurantOrder = getRestaurantOrderRandomSampleGenerator();
        RestaurantTable restaurantTableBack = getRestaurantTableRandomSampleGenerator();

        restaurantOrder.setTable(restaurantTableBack);
        assertThat(restaurantOrder.getTable()).isEqualTo(restaurantTableBack);

        restaurantOrder.table(null);
        assertThat(restaurantOrder.getTable()).isNull();
    }

    @Test
    void promotionTest() {
        RestaurantOrder restaurantOrder = getRestaurantOrderRandomSampleGenerator();
        Promotion promotionBack = getPromotionRandomSampleGenerator();

        restaurantOrder.setPromotion(promotionBack);
        assertThat(restaurantOrder.getPromotion()).isEqualTo(promotionBack);

        restaurantOrder.promotion(null);
        assertThat(restaurantOrder.getPromotion()).isNull();
    }

    @Test
    void reservationTest() {
        RestaurantOrder restaurantOrder = getRestaurantOrderRandomSampleGenerator();
        Reservation reservationBack = getReservationRandomSampleGenerator();

        restaurantOrder.setReservation(reservationBack);
        assertThat(restaurantOrder.getReservation()).isEqualTo(reservationBack);

        restaurantOrder.reservation(null);
        assertThat(restaurantOrder.getReservation()).isNull();
    }
}
