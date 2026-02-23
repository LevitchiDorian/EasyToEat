package md.utm.restaurant.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DiningRoomDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DiningRoomDTO.class);
        DiningRoomDTO diningRoomDTO1 = new DiningRoomDTO();
        diningRoomDTO1.setId(1L);
        DiningRoomDTO diningRoomDTO2 = new DiningRoomDTO();
        assertThat(diningRoomDTO1).isNotEqualTo(diningRoomDTO2);
        diningRoomDTO2.setId(diningRoomDTO1.getId());
        assertThat(diningRoomDTO1).isEqualTo(diningRoomDTO2);
        diningRoomDTO2.setId(2L);
        assertThat(diningRoomDTO1).isNotEqualTo(diningRoomDTO2);
        diningRoomDTO1.setId(null);
        assertThat(diningRoomDTO1).isNotEqualTo(diningRoomDTO2);
    }
}
