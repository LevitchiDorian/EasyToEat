package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.LocationTestSamples.*;
import static md.utm.restaurant.domain.NotificationTestSamples.*;
import static md.utm.restaurant.domain.ReservationTestSamples.*;
import static md.utm.restaurant.domain.RestaurantOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notification.class);
        Notification notification1 = getNotificationSample1();
        Notification notification2 = new Notification();
        assertThat(notification1).isNotEqualTo(notification2);

        notification2.setId(notification1.getId());
        assertThat(notification1).isEqualTo(notification2);

        notification2 = getNotificationSample2();
        assertThat(notification1).isNotEqualTo(notification2);
    }

    @Test
    void locationTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        notification.setLocation(locationBack);
        assertThat(notification.getLocation()).isEqualTo(locationBack);

        notification.location(null);
        assertThat(notification.getLocation()).isNull();
    }

    @Test
    void reservationTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        Reservation reservationBack = getReservationRandomSampleGenerator();

        notification.setReservation(reservationBack);
        assertThat(notification.getReservation()).isEqualTo(reservationBack);

        notification.reservation(null);
        assertThat(notification.getReservation()).isNull();
    }

    @Test
    void orderTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        RestaurantOrder restaurantOrderBack = getRestaurantOrderRandomSampleGenerator();

        notification.setOrder(restaurantOrderBack);
        assertThat(notification.getOrder()).isEqualTo(restaurantOrderBack);

        notification.order(null);
        assertThat(notification.getOrder()).isNull();
    }
}
