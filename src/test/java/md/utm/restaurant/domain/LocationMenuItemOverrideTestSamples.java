package md.utm.restaurant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LocationMenuItemOverrideTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static LocationMenuItemOverride getLocationMenuItemOverrideSample1() {
        return new LocationMenuItemOverride().id(1L).preparationTimeOverride(1).notes("notes1");
    }

    public static LocationMenuItemOverride getLocationMenuItemOverrideSample2() {
        return new LocationMenuItemOverride().id(2L).preparationTimeOverride(2).notes("notes2");
    }

    public static LocationMenuItemOverride getLocationMenuItemOverrideRandomSampleGenerator() {
        return new LocationMenuItemOverride()
            .id(longCount.incrementAndGet())
            .preparationTimeOverride(intCount.incrementAndGet())
            .notes(UUID.randomUUID().toString());
    }
}
