package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.ReservationTableAsserts.*;
import static md.utm.restaurant.domain.ReservationTableTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReservationTableMapperTest {

    private ReservationTableMapper reservationTableMapper;

    @BeforeEach
    void setUp() {
        reservationTableMapper = new ReservationTableMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReservationTableSample1();
        var actual = reservationTableMapper.toEntity(reservationTableMapper.toDto(expected));
        assertReservationTableAllPropertiesEquals(expected, actual);
    }
}
