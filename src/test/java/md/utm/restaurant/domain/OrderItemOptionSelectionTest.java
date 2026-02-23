package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.MenuItemOptionValueTestSamples.*;
import static md.utm.restaurant.domain.OrderItemOptionSelectionTestSamples.*;
import static md.utm.restaurant.domain.OrderItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderItemOptionSelectionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItemOptionSelection.class);
        OrderItemOptionSelection orderItemOptionSelection1 = getOrderItemOptionSelectionSample1();
        OrderItemOptionSelection orderItemOptionSelection2 = new OrderItemOptionSelection();
        assertThat(orderItemOptionSelection1).isNotEqualTo(orderItemOptionSelection2);

        orderItemOptionSelection2.setId(orderItemOptionSelection1.getId());
        assertThat(orderItemOptionSelection1).isEqualTo(orderItemOptionSelection2);

        orderItemOptionSelection2 = getOrderItemOptionSelectionSample2();
        assertThat(orderItemOptionSelection1).isNotEqualTo(orderItemOptionSelection2);
    }

    @Test
    void optionValueTest() {
        OrderItemOptionSelection orderItemOptionSelection = getOrderItemOptionSelectionRandomSampleGenerator();
        MenuItemOptionValue menuItemOptionValueBack = getMenuItemOptionValueRandomSampleGenerator();

        orderItemOptionSelection.setOptionValue(menuItemOptionValueBack);
        assertThat(orderItemOptionSelection.getOptionValue()).isEqualTo(menuItemOptionValueBack);

        orderItemOptionSelection.optionValue(null);
        assertThat(orderItemOptionSelection.getOptionValue()).isNull();
    }

    @Test
    void orderItemTest() {
        OrderItemOptionSelection orderItemOptionSelection = getOrderItemOptionSelectionRandomSampleGenerator();
        OrderItem orderItemBack = getOrderItemRandomSampleGenerator();

        orderItemOptionSelection.setOrderItem(orderItemBack);
        assertThat(orderItemOptionSelection.getOrderItem()).isEqualTo(orderItemBack);

        orderItemOptionSelection.orderItem(null);
        assertThat(orderItemOptionSelection.getOrderItem()).isNull();
    }
}
