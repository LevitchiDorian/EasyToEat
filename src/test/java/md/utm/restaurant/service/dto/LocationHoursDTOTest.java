package md.utm.restaurant.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LocationHoursDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LocationHoursDTO.class);
        LocationHoursDTO locationHoursDTO1 = new LocationHoursDTO();
        locationHoursDTO1.setId(1L);
        LocationHoursDTO locationHoursDTO2 = new LocationHoursDTO();
        assertThat(locationHoursDTO1).isNotEqualTo(locationHoursDTO2);
        locationHoursDTO2.setId(locationHoursDTO1.getId());
        assertThat(locationHoursDTO1).isEqualTo(locationHoursDTO2);
        locationHoursDTO2.setId(2L);
        assertThat(locationHoursDTO1).isNotEqualTo(locationHoursDTO2);
        locationHoursDTO1.setId(null);
        assertThat(locationHoursDTO1).isNotEqualTo(locationHoursDTO2);
    }
}
