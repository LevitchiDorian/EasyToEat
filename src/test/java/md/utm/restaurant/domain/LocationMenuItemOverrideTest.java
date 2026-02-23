package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.LocationMenuItemOverrideTestSamples.*;
import static md.utm.restaurant.domain.LocationTestSamples.*;
import static md.utm.restaurant.domain.MenuItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LocationMenuItemOverrideTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LocationMenuItemOverride.class);
        LocationMenuItemOverride locationMenuItemOverride1 = getLocationMenuItemOverrideSample1();
        LocationMenuItemOverride locationMenuItemOverride2 = new LocationMenuItemOverride();
        assertThat(locationMenuItemOverride1).isNotEqualTo(locationMenuItemOverride2);

        locationMenuItemOverride2.setId(locationMenuItemOverride1.getId());
        assertThat(locationMenuItemOverride1).isEqualTo(locationMenuItemOverride2);

        locationMenuItemOverride2 = getLocationMenuItemOverrideSample2();
        assertThat(locationMenuItemOverride1).isNotEqualTo(locationMenuItemOverride2);
    }

    @Test
    void menuItemTest() {
        LocationMenuItemOverride locationMenuItemOverride = getLocationMenuItemOverrideRandomSampleGenerator();
        MenuItem menuItemBack = getMenuItemRandomSampleGenerator();

        locationMenuItemOverride.setMenuItem(menuItemBack);
        assertThat(locationMenuItemOverride.getMenuItem()).isEqualTo(menuItemBack);

        locationMenuItemOverride.menuItem(null);
        assertThat(locationMenuItemOverride.getMenuItem()).isNull();
    }

    @Test
    void locationTest() {
        LocationMenuItemOverride locationMenuItemOverride = getLocationMenuItemOverrideRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        locationMenuItemOverride.setLocation(locationBack);
        assertThat(locationMenuItemOverride.getLocation()).isEqualTo(locationBack);

        locationMenuItemOverride.location(null);
        assertThat(locationMenuItemOverride.getLocation()).isNull();
    }
}
