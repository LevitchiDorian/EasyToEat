package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.BrandTestSamples.*;
import static md.utm.restaurant.domain.DiningRoomTestSamples.*;
import static md.utm.restaurant.domain.LocationHoursTestSamples.*;
import static md.utm.restaurant.domain.LocationMenuItemOverrideTestSamples.*;
import static md.utm.restaurant.domain.LocationTestSamples.*;
import static md.utm.restaurant.domain.PromotionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LocationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Location.class);
        Location location1 = getLocationSample1();
        Location location2 = new Location();
        assertThat(location1).isNotEqualTo(location2);

        location2.setId(location1.getId());
        assertThat(location1).isEqualTo(location2);

        location2 = getLocationSample2();
        assertThat(location1).isNotEqualTo(location2);
    }

    @Test
    void hoursTest() {
        Location location = getLocationRandomSampleGenerator();
        LocationHours locationHoursBack = getLocationHoursRandomSampleGenerator();

        location.addHours(locationHoursBack);
        assertThat(location.getHours()).containsOnly(locationHoursBack);
        assertThat(locationHoursBack.getLocation()).isEqualTo(location);

        location.removeHours(locationHoursBack);
        assertThat(location.getHours()).doesNotContain(locationHoursBack);
        assertThat(locationHoursBack.getLocation()).isNull();

        location.hours(new HashSet<>(Set.of(locationHoursBack)));
        assertThat(location.getHours()).containsOnly(locationHoursBack);
        assertThat(locationHoursBack.getLocation()).isEqualTo(location);

        location.setHours(new HashSet<>());
        assertThat(location.getHours()).doesNotContain(locationHoursBack);
        assertThat(locationHoursBack.getLocation()).isNull();
    }

    @Test
    void roomsTest() {
        Location location = getLocationRandomSampleGenerator();
        DiningRoom diningRoomBack = getDiningRoomRandomSampleGenerator();

        location.addRooms(diningRoomBack);
        assertThat(location.getRooms()).containsOnly(diningRoomBack);
        assertThat(diningRoomBack.getLocation()).isEqualTo(location);

        location.removeRooms(diningRoomBack);
        assertThat(location.getRooms()).doesNotContain(diningRoomBack);
        assertThat(diningRoomBack.getLocation()).isNull();

        location.rooms(new HashSet<>(Set.of(diningRoomBack)));
        assertThat(location.getRooms()).containsOnly(diningRoomBack);
        assertThat(diningRoomBack.getLocation()).isEqualTo(location);

        location.setRooms(new HashSet<>());
        assertThat(location.getRooms()).doesNotContain(diningRoomBack);
        assertThat(diningRoomBack.getLocation()).isNull();
    }

    @Test
    void localPromotionsTest() {
        Location location = getLocationRandomSampleGenerator();
        Promotion promotionBack = getPromotionRandomSampleGenerator();

        location.addLocalPromotions(promotionBack);
        assertThat(location.getLocalPromotions()).containsOnly(promotionBack);
        assertThat(promotionBack.getLocation()).isEqualTo(location);

        location.removeLocalPromotions(promotionBack);
        assertThat(location.getLocalPromotions()).doesNotContain(promotionBack);
        assertThat(promotionBack.getLocation()).isNull();

        location.localPromotions(new HashSet<>(Set.of(promotionBack)));
        assertThat(location.getLocalPromotions()).containsOnly(promotionBack);
        assertThat(promotionBack.getLocation()).isEqualTo(location);

        location.setLocalPromotions(new HashSet<>());
        assertThat(location.getLocalPromotions()).doesNotContain(promotionBack);
        assertThat(promotionBack.getLocation()).isNull();
    }

    @Test
    void menuOverridesTest() {
        Location location = getLocationRandomSampleGenerator();
        LocationMenuItemOverride locationMenuItemOverrideBack = getLocationMenuItemOverrideRandomSampleGenerator();

        location.addMenuOverrides(locationMenuItemOverrideBack);
        assertThat(location.getMenuOverrides()).containsOnly(locationMenuItemOverrideBack);
        assertThat(locationMenuItemOverrideBack.getLocation()).isEqualTo(location);

        location.removeMenuOverrides(locationMenuItemOverrideBack);
        assertThat(location.getMenuOverrides()).doesNotContain(locationMenuItemOverrideBack);
        assertThat(locationMenuItemOverrideBack.getLocation()).isNull();

        location.menuOverrides(new HashSet<>(Set.of(locationMenuItemOverrideBack)));
        assertThat(location.getMenuOverrides()).containsOnly(locationMenuItemOverrideBack);
        assertThat(locationMenuItemOverrideBack.getLocation()).isEqualTo(location);

        location.setMenuOverrides(new HashSet<>());
        assertThat(location.getMenuOverrides()).doesNotContain(locationMenuItemOverrideBack);
        assertThat(locationMenuItemOverrideBack.getLocation()).isNull();
    }

    @Test
    void brandTest() {
        Location location = getLocationRandomSampleGenerator();
        Brand brandBack = getBrandRandomSampleGenerator();

        location.setBrand(brandBack);
        assertThat(location.getBrand()).isEqualTo(brandBack);

        location.brand(null);
        assertThat(location.getBrand()).isNull();
    }
}
