package md.utm.restaurant.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReservationTableDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReservationTableDTO.class);
        ReservationTableDTO reservationTableDTO1 = new ReservationTableDTO();
        reservationTableDTO1.setId(1L);
        ReservationTableDTO reservationTableDTO2 = new ReservationTableDTO();
        assertThat(reservationTableDTO1).isNotEqualTo(reservationTableDTO2);
        reservationTableDTO2.setId(reservationTableDTO1.getId());
        assertThat(reservationTableDTO1).isEqualTo(reservationTableDTO2);
        reservationTableDTO2.setId(2L);
        assertThat(reservationTableDTO1).isNotEqualTo(reservationTableDTO2);
        reservationTableDTO1.setId(null);
        assertThat(reservationTableDTO1).isNotEqualTo(reservationTableDTO2);
    }
}
