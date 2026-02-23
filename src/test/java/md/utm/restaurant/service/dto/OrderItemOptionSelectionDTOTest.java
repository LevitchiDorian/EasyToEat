package md.utm.restaurant.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderItemOptionSelectionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItemOptionSelectionDTO.class);
        OrderItemOptionSelectionDTO orderItemOptionSelectionDTO1 = new OrderItemOptionSelectionDTO();
        orderItemOptionSelectionDTO1.setId(1L);
        OrderItemOptionSelectionDTO orderItemOptionSelectionDTO2 = new OrderItemOptionSelectionDTO();
        assertThat(orderItemOptionSelectionDTO1).isNotEqualTo(orderItemOptionSelectionDTO2);
        orderItemOptionSelectionDTO2.setId(orderItemOptionSelectionDTO1.getId());
        assertThat(orderItemOptionSelectionDTO1).isEqualTo(orderItemOptionSelectionDTO2);
        orderItemOptionSelectionDTO2.setId(2L);
        assertThat(orderItemOptionSelectionDTO1).isNotEqualTo(orderItemOptionSelectionDTO2);
        orderItemOptionSelectionDTO1.setId(null);
        assertThat(orderItemOptionSelectionDTO1).isNotEqualTo(orderItemOptionSelectionDTO2);
    }
}
