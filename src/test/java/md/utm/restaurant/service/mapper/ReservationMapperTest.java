package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.ReservationAsserts.*;
import static md.utm.restaurant.domain.ReservationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReservationMapperTest {

    private ReservationMapper reservationMapper;

    @BeforeEach
    void setUp() {
        reservationMapper = new ReservationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReservationSample1();
        var actual = reservationMapper.toEntity(reservationMapper.toDto(expected));
        assertReservationAllPropertiesEquals(expected, actual);
    }
}
