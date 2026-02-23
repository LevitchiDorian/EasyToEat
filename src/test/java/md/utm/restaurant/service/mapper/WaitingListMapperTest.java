package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.WaitingListAsserts.*;
import static md.utm.restaurant.domain.WaitingListTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WaitingListMapperTest {

    private WaitingListMapper waitingListMapper;

    @BeforeEach
    void setUp() {
        waitingListMapper = new WaitingListMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWaitingListSample1();
        var actual = waitingListMapper.toEntity(waitingListMapper.toDto(expected));
        assertWaitingListAllPropertiesEquals(expected, actual);
    }
}
