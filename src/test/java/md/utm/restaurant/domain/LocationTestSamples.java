package md.utm.restaurant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LocationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Location getLocationSample1() {
        return new Location()
            .id(1L)
            .name("name1")
            .address("address1")
            .city("city1")
            .phone("phone1")
            .email("email1")
            .reservationDurationOverride(1)
            .maxAdvanceBookingDaysOverride(1)
            .cancellationDeadlineOverride(1);
    }

    public static Location getLocationSample2() {
        return new Location()
            .id(2L)
            .name("name2")
            .address("address2")
            .city("city2")
            .phone("phone2")
            .email("email2")
            .reservationDurationOverride(2)
            .maxAdvanceBookingDaysOverride(2)
            .cancellationDeadlineOverride(2);
    }

    public static Location getLocationRandomSampleGenerator() {
        return new Location()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .reservationDurationOverride(intCount.incrementAndGet())
            .maxAdvanceBookingDaysOverride(intCount.incrementAndGet())
            .cancellationDeadlineOverride(intCount.incrementAndGet());
    }
}
