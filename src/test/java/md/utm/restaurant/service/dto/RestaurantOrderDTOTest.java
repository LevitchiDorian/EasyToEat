package md.utm.restaurant.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RestaurantOrderDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestaurantOrderDTO.class);
        RestaurantOrderDTO restaurantOrderDTO1 = new RestaurantOrderDTO();
        restaurantOrderDTO1.setId(1L);
        RestaurantOrderDTO restaurantOrderDTO2 = new RestaurantOrderDTO();
        assertThat(restaurantOrderDTO1).isNotEqualTo(restaurantOrderDTO2);
        restaurantOrderDTO2.setId(restaurantOrderDTO1.getId());
        assertThat(restaurantOrderDTO1).isEqualTo(restaurantOrderDTO2);
        restaurantOrderDTO2.setId(2L);
        assertThat(restaurantOrderDTO1).isNotEqualTo(restaurantOrderDTO2);
        restaurantOrderDTO1.setId(null);
        assertThat(restaurantOrderDTO1).isNotEqualTo(restaurantOrderDTO2);
    }
}
