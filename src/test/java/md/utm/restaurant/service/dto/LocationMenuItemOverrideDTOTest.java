package md.utm.restaurant.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LocationMenuItemOverrideDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LocationMenuItemOverrideDTO.class);
        LocationMenuItemOverrideDTO locationMenuItemOverrideDTO1 = new LocationMenuItemOverrideDTO();
        locationMenuItemOverrideDTO1.setId(1L);
        LocationMenuItemOverrideDTO locationMenuItemOverrideDTO2 = new LocationMenuItemOverrideDTO();
        assertThat(locationMenuItemOverrideDTO1).isNotEqualTo(locationMenuItemOverrideDTO2);
        locationMenuItemOverrideDTO2.setId(locationMenuItemOverrideDTO1.getId());
        assertThat(locationMenuItemOverrideDTO1).isEqualTo(locationMenuItemOverrideDTO2);
        locationMenuItemOverrideDTO2.setId(2L);
        assertThat(locationMenuItemOverrideDTO1).isNotEqualTo(locationMenuItemOverrideDTO2);
        locationMenuItemOverrideDTO1.setId(null);
        assertThat(locationMenuItemOverrideDTO1).isNotEqualTo(locationMenuItemOverrideDTO2);
    }
}
