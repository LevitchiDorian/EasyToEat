package md.utm.restaurant.service.mapper;

import static md.utm.restaurant.domain.ReviewAsserts.*;
import static md.utm.restaurant.domain.ReviewTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReviewMapperTest {

    private ReviewMapper reviewMapper;

    @BeforeEach
    void setUp() {
        reviewMapper = new ReviewMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReviewSample1();
        var actual = reviewMapper.toEntity(reviewMapper.toDto(expected));
        assertReviewAllPropertiesEquals(expected, actual);
    }
}
