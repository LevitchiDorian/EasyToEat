package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.PaymentTestSamples.*;
import static md.utm.restaurant.domain.RestaurantOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Payment.class);
        Payment payment1 = getPaymentSample1();
        Payment payment2 = new Payment();
        assertThat(payment1).isNotEqualTo(payment2);

        payment2.setId(payment1.getId());
        assertThat(payment1).isEqualTo(payment2);

        payment2 = getPaymentSample2();
        assertThat(payment1).isNotEqualTo(payment2);
    }

    @Test
    void orderTest() {
        Payment payment = getPaymentRandomSampleGenerator();
        RestaurantOrder restaurantOrderBack = getRestaurantOrderRandomSampleGenerator();

        payment.setOrder(restaurantOrderBack);
        assertThat(payment.getOrder()).isEqualTo(restaurantOrderBack);

        payment.order(null);
        assertThat(payment.getOrder()).isNull();
    }
}
