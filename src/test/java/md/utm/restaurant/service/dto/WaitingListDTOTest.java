package md.utm.restaurant.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WaitingListDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WaitingListDTO.class);
        WaitingListDTO waitingListDTO1 = new WaitingListDTO();
        waitingListDTO1.setId(1L);
        WaitingListDTO waitingListDTO2 = new WaitingListDTO();
        assertThat(waitingListDTO1).isNotEqualTo(waitingListDTO2);
        waitingListDTO2.setId(waitingListDTO1.getId());
        assertThat(waitingListDTO1).isEqualTo(waitingListDTO2);
        waitingListDTO2.setId(2L);
        assertThat(waitingListDTO1).isNotEqualTo(waitingListDTO2);
        waitingListDTO1.setId(null);
        assertThat(waitingListDTO1).isNotEqualTo(waitingListDTO2);
    }
}
