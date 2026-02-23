package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.LocationHoursTestSamples.*;
import static md.utm.restaurant.domain.LocationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LocationHoursTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LocationHours.class);
        LocationHours locationHours1 = getLocationHoursSample1();
        LocationHours locationHours2 = new LocationHours();
        assertThat(locationHours1).isNotEqualTo(locationHours2);

        locationHours2.setId(locationHours1.getId());
        assertThat(locationHours1).isEqualTo(locationHours2);

        locationHours2 = getLocationHoursSample2();
        assertThat(locationHours1).isNotEqualTo(locationHours2);
    }

    @Test
    void locationTest() {
        LocationHours locationHours = getLocationHoursRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        locationHours.setLocation(locationBack);
        assertThat(locationHours.getLocation()).isEqualTo(locationBack);

        locationHours.location(null);
        assertThat(locationHours.getLocation()).isNull();
    }
}
