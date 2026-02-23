package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.LocationHoursAsserts.*;
import static md.utm.restaurant.domain.LocationHoursTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocationHoursMapperTest {

    private LocationHoursMapper locationHoursMapper;

    @BeforeEach
    void setUp() {
        locationHoursMapper = new LocationHoursMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLocationHoursSample1();
        var actual = locationHoursMapper.toEntity(locationHoursMapper.toDto(expected));
        assertLocationHoursAllPropertiesEquals(expected, actual);
    }
}
