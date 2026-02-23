package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.MenuItemTestSamples.*;
import static md.utm.restaurant.domain.OrderItemOptionSelectionTestSamples.*;
import static md.utm.restaurant.domain.OrderItemTestSamples.*;
import static md.utm.restaurant.domain.RestaurantOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItem.class);
        OrderItem orderItem1 = getOrderItemSample1();
        OrderItem orderItem2 = new OrderItem();
        assertThat(orderItem1).isNotEqualTo(orderItem2);

        orderItem2.setId(orderItem1.getId());
        assertThat(orderItem1).isEqualTo(orderItem2);

        orderItem2 = getOrderItemSample2();
        assertThat(orderItem1).isNotEqualTo(orderItem2);
    }

    @Test
    void optionSelectionsTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        OrderItemOptionSelection orderItemOptionSelectionBack = getOrderItemOptionSelectionRandomSampleGenerator();

        orderItem.addOptionSelections(orderItemOptionSelectionBack);
        assertThat(orderItem.getOptionSelections()).containsOnly(orderItemOptionSelectionBack);
        assertThat(orderItemOptionSelectionBack.getOrderItem()).isEqualTo(orderItem);

        orderItem.removeOptionSelections(orderItemOptionSelectionBack);
        assertThat(orderItem.getOptionSelections()).doesNotContain(orderItemOptionSelectionBack);
        assertThat(orderItemOptionSelectionBack.getOrderItem()).isNull();

        orderItem.optionSelections(new HashSet<>(Set.of(orderItemOptionSelectionBack)));
        assertThat(orderItem.getOptionSelections()).containsOnly(orderItemOptionSelectionBack);
        assertThat(orderItemOptionSelectionBack.getOrderItem()).isEqualTo(orderItem);

        orderItem.setOptionSelections(new HashSet<>());
        assertThat(orderItem.getOptionSelections()).doesNotContain(orderItemOptionSelectionBack);
        assertThat(orderItemOptionSelectionBack.getOrderItem()).isNull();
    }

    @Test
    void menuItemTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        MenuItem menuItemBack = getMenuItemRandomSampleGenerator();

        orderItem.setMenuItem(menuItemBack);
        assertThat(orderItem.getMenuItem()).isEqualTo(menuItemBack);

        orderItem.menuItem(null);
        assertThat(orderItem.getMenuItem()).isNull();
    }

    @Test
    void orderTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        RestaurantOrder restaurantOrderBack = getRestaurantOrderRandomSampleGenerator();

        orderItem.setOrder(restaurantOrderBack);
        assertThat(orderItem.getOrder()).isEqualTo(restaurantOrderBack);

        orderItem.order(null);
        assertThat(orderItem.getOrder()).isNull();
    }
}
