package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.DiningRoomTestSamples.*;
import static md.utm.restaurant.domain.LocationTestSamples.*;
import static md.utm.restaurant.domain.RestaurantTableTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DiningRoomTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DiningRoom.class);
        DiningRoom diningRoom1 = getDiningRoomSample1();
        DiningRoom diningRoom2 = new DiningRoom();
        assertThat(diningRoom1).isNotEqualTo(diningRoom2);

        diningRoom2.setId(diningRoom1.getId());
        assertThat(diningRoom1).isEqualTo(diningRoom2);

        diningRoom2 = getDiningRoomSample2();
        assertThat(diningRoom1).isNotEqualTo(diningRoom2);
    }

    @Test
    void tablesTest() {
        DiningRoom diningRoom = getDiningRoomRandomSampleGenerator();
        RestaurantTable restaurantTableBack = getRestaurantTableRandomSampleGenerator();

        diningRoom.addTables(restaurantTableBack);
        assertThat(diningRoom.getTables()).containsOnly(restaurantTableBack);
        assertThat(restaurantTableBack.getRoom()).isEqualTo(diningRoom);

        diningRoom.removeTables(restaurantTableBack);
        assertThat(diningRoom.getTables()).doesNotContain(restaurantTableBack);
        assertThat(restaurantTableBack.getRoom()).isNull();

        diningRoom.tables(new HashSet<>(Set.of(restaurantTableBack)));
        assertThat(diningRoom.getTables()).containsOnly(restaurantTableBack);
        assertThat(restaurantTableBack.getRoom()).isEqualTo(diningRoom);

        diningRoom.setTables(new HashSet<>());
        assertThat(diningRoom.getTables()).doesNotContain(restaurantTableBack);
        assertThat(restaurantTableBack.getRoom()).isNull();
    }

    @Test
    void locationTest() {
        DiningRoom diningRoom = getDiningRoomRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        diningRoom.setLocation(locationBack);
        assertThat(diningRoom.getLocation()).isEqualTo(locationBack);

        diningRoom.location(null);
        assertThat(diningRoom.getLocation()).isNull();
    }
}
