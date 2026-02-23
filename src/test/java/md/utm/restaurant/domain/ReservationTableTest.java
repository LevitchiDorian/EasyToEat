package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.ReservationTableTestSamples.*;
import static md.utm.restaurant.domain.ReservationTestSamples.*;
import static md.utm.restaurant.domain.RestaurantTableTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReservationTableTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReservationTable.class);
        ReservationTable reservationTable1 = getReservationTableSample1();
        ReservationTable reservationTable2 = new ReservationTable();
        assertThat(reservationTable1).isNotEqualTo(reservationTable2);

        reservationTable2.setId(reservationTable1.getId());
        assertThat(reservationTable1).isEqualTo(reservationTable2);

        reservationTable2 = getReservationTableSample2();
        assertThat(reservationTable1).isNotEqualTo(reservationTable2);
    }

    @Test
    void tableTest() {
        ReservationTable reservationTable = getReservationTableRandomSampleGenerator();
        RestaurantTable restaurantTableBack = getRestaurantTableRandomSampleGenerator();

        reservationTable.setTable(restaurantTableBack);
        assertThat(reservationTable.getTable()).isEqualTo(restaurantTableBack);

        reservationTable.table(null);
        assertThat(reservationTable.getTable()).isNull();
    }

    @Test
    void reservationTest() {
        ReservationTable reservationTable = getReservationTableRandomSampleGenerator();
        Reservation reservationBack = getReservationRandomSampleGenerator();

        reservationTable.setReservation(reservationBack);
        assertThat(reservationTable.getReservation()).isEqualTo(reservationBack);

        reservationTable.reservation(null);
        assertThat(reservationTable.getReservation()).isNull();
    }
}
