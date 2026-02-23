package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.LocationMenuItemOverrideAsserts.*;
import static md.utm.restaurant.domain.LocationMenuItemOverrideTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocationMenuItemOverrideMapperTest {

    private LocationMenuItemOverrideMapper locationMenuItemOverrideMapper;

    @BeforeEach
    void setUp() {
        locationMenuItemOverrideMapper = new LocationMenuItemOverrideMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLocationMenuItemOverrideSample1();
        var actual = locationMenuItemOverrideMapper.toEntity(locationMenuItemOverrideMapper.toDto(expected));
        assertLocationMenuItemOverrideAllPropertiesEquals(expected, actual);
    }
}
