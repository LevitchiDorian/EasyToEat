package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.LocationAsserts.*;
import static md.utm.restaurant.domain.LocationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocationMapperTest {

    private LocationMapper locationMapper;

    @BeforeEach
    void setUp() {
        locationMapper = new LocationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLocationSample1();
        var actual = locationMapper.toEntity(locationMapper.toDto(expected));
        assertLocationAllPropertiesEquals(expected, actual);
    }
}
