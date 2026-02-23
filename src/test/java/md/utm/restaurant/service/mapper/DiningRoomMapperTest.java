package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.DiningRoomAsserts.*;
import static md.utm.restaurant.domain.DiningRoomTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiningRoomMapperTest {

    private DiningRoomMapper diningRoomMapper;

    @BeforeEach
    void setUp() {
        diningRoomMapper = new DiningRoomMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDiningRoomSample1();
        var actual = diningRoomMapper.toEntity(diningRoomMapper.toDto(expected));
        assertDiningRoomAllPropertiesEquals(expected, actual);
    }
}
