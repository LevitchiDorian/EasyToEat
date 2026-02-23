package md.utm.restaurant.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ReviewTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Review getReviewSample1() {
        return new Review().id(1L).overallRating(1).foodRating(1).serviceRating(1).ambienceRating(1);
    }

    public static Review getReviewSample2() {
        return new Review().id(2L).overallRating(2).foodRating(2).serviceRating(2).ambienceRating(2);
    }

    public static Review getReviewRandomSampleGenerator() {
        return new Review()
            .id(longCount.incrementAndGet())
            .overallRating(intCount.incrementAndGet())
            .foodRating(intCount.incrementAndGet())
            .serviceRating(intCount.incrementAndGet())
            .ambienceRating(intCount.incrementAndGet());
    }
}
