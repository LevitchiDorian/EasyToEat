package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.DiningRoomTestSamples.*;
import static md.utm.restaurant.domain.LocationTestSamples.*;
import static md.utm.restaurant.domain.ReservationTableTestSamples.*;
import static md.utm.restaurant.domain.ReservationTestSamples.*;
import static md.utm.restaurant.domain.RestaurantOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReservationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Reservation.class);
        Reservation reservation1 = getReservationSample1();
        Reservation reservation2 = new Reservation();
        assertThat(reservation1).isNotEqualTo(reservation2);

        reservation2.setId(reservation1.getId());
        assertThat(reservation1).isEqualTo(reservation2);

        reservation2 = getReservationSample2();
        assertThat(reservation1).isNotEqualTo(reservation2);
    }

    @Test
    void tableAssignmentsTest() {
        Reservation reservation = getReservationRandomSampleGenerator();
        ReservationTable reservationTableBack = getReservationTableRandomSampleGenerator();

        reservation.addTableAssignments(reservationTableBack);
        assertThat(reservation.getTableAssignments()).containsOnly(reservationTableBack);
        assertThat(reservationTableBack.getReservation()).isEqualTo(reservation);

        reservation.removeTableAssignments(reservationTableBack);
        assertThat(reservation.getTableAssignments()).doesNotContain(reservationTableBack);
        assertThat(reservationTableBack.getReservation()).isNull();

        reservation.tableAssignments(new HashSet<>(Set.of(reservationTableBack)));
        assertThat(reservation.getTableAssignments()).containsOnly(reservationTableBack);
        assertThat(reservationTableBack.getReservation()).isEqualTo(reservation);

        reservation.setTableAssignments(new HashSet<>());
        assertThat(reservation.getTableAssignments()).doesNotContain(reservationTableBack);
        assertThat(reservationTableBack.getReservation()).isNull();
    }

    @Test
    void ordersTest() {
        Reservation reservation = getReservationRandomSampleGenerator();
        RestaurantOrder restaurantOrderBack = getRestaurantOrderRandomSampleGenerator();

        reservation.addOrders(restaurantOrderBack);
        assertThat(reservation.getOrders()).containsOnly(restaurantOrderBack);
        assertThat(restaurantOrderBack.getReservation()).isEqualTo(reservation);

        reservation.removeOrders(restaurantOrderBack);
        assertThat(reservation.getOrders()).doesNotContain(restaurantOrderBack);
        assertThat(restaurantOrderBack.getReservation()).isNull();

        reservation.orders(new HashSet<>(Set.of(restaurantOrderBack)));
        assertThat(reservation.getOrders()).containsOnly(restaurantOrderBack);
        assertThat(restaurantOrderBack.getReservation()).isEqualTo(reservation);

        reservation.setOrders(new HashSet<>());
        assertThat(reservation.getOrders()).doesNotContain(restaurantOrderBack);
        assertThat(restaurantOrderBack.getReservation()).isNull();
    }

    @Test
    void locationTest() {
        Reservation reservation = getReservationRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        reservation.setLocation(locationBack);
        assertThat(reservation.getLocation()).isEqualTo(locationBack);

        reservation.location(null);
        assertThat(reservation.getLocation()).isNull();
    }

    @Test
    void roomTest() {
        Reservation reservation = getReservationRandomSampleGenerator();
        DiningRoom diningRoomBack = getDiningRoomRandomSampleGenerator();

        reservation.setRoom(diningRoomBack);
        assertThat(reservation.getRoom()).isEqualTo(diningRoomBack);

        reservation.room(null);
        assertThat(reservation.getRoom()).isNull();
    }
}
