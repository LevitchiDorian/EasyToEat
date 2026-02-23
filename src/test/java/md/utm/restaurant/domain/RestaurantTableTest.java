package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.DiningRoomTestSamples.*;
import static md.utm.restaurant.domain.RestaurantTableTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RestaurantTableTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestaurantTable.class);
        RestaurantTable restaurantTable1 = getRestaurantTableSample1();
        RestaurantTable restaurantTable2 = new RestaurantTable();
        assertThat(restaurantTable1).isNotEqualTo(restaurantTable2);

        restaurantTable2.setId(restaurantTable1.getId());
        assertThat(restaurantTable1).isEqualTo(restaurantTable2);

        restaurantTable2 = getRestaurantTableSample2();
        assertThat(restaurantTable1).isNotEqualTo(restaurantTable2);
    }

    @Test
    void roomTest() {
        RestaurantTable restaurantTable = getRestaurantTableRandomSampleGenerator();
        DiningRoom diningRoomBack = getDiningRoomRandomSampleGenerator();

        restaurantTable.setRoom(diningRoomBack);
        assertThat(restaurantTable.getRoom()).isEqualTo(diningRoomBack);

        restaurantTable.room(null);
        assertThat(restaurantTable.getRoom()).isNull();
    }
}
