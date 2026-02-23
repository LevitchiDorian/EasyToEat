package md.utm.restaurant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LocationHoursTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static LocationHours getLocationHoursSample1() {
        return new LocationHours().id(1L).openTime("openTime1").closeTime("closeTime1").specialNote("specialNote1");
    }

    public static LocationHours getLocationHoursSample2() {
        return new LocationHours().id(2L).openTime("openTime2").closeTime("closeTime2").specialNote("specialNote2");
    }

    public static LocationHours getLocationHoursRandomSampleGenerator() {
        return new LocationHours()
            .id(longCount.incrementAndGet())
            .openTime(UUID.randomUUID().toString())
            .closeTime(UUID.randomUUID().toString())
            .specialNote(UUID.randomUUID().toString());
    }
}
