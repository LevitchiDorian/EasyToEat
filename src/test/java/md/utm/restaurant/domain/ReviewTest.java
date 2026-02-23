package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.LocationTestSamples.*;
import static md.utm.restaurant.domain.ReservationTestSamples.*;
import static md.utm.restaurant.domain.ReviewTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Review.class);
        Review review1 = getReviewSample1();
        Review review2 = new Review();
        assertThat(review1).isNotEqualTo(review2);

        review2.setId(review1.getId());
        assertThat(review1).isEqualTo(review2);

        review2 = getReviewSample2();
        assertThat(review1).isNotEqualTo(review2);
    }

    @Test
    void locationTest() {
        Review review = getReviewRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        review.setLocation(locationBack);
        assertThat(review.getLocation()).isEqualTo(locationBack);

        review.location(null);
        assertThat(review.getLocation()).isNull();
    }

    @Test
    void reservationTest() {
        Review review = getReviewRandomSampleGenerator();
        Reservation reservationBack = getReservationRandomSampleGenerator();

        review.setReservation(reservationBack);
        assertThat(review.getReservation()).isEqualTo(reservationBack);

        review.reservation(null);
        assertThat(review.getReservation()).isNull();
    }
}
