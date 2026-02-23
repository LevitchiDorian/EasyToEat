package md.utm.restaurant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BrandTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Brand getBrandSample1() {
        return new Brand()
            .id(1L)
            .name("name1")
            .logoUrl("logoUrl1")
            .coverImageUrl("coverImageUrl1")
            .primaryColor("primaryColor1")
            .secondaryColor("secondaryColor1")
            .website("website1")
            .contactEmail("contactEmail1")
            .contactPhone("contactPhone1")
            .defaultReservationDuration(1)
            .maxAdvanceBookingDays(1)
            .cancellationDeadlineHours(1);
    }

    public static Brand getBrandSample2() {
        return new Brand()
            .id(2L)
            .name("name2")
            .logoUrl("logoUrl2")
            .coverImageUrl("coverImageUrl2")
            .primaryColor("primaryColor2")
            .secondaryColor("secondaryColor2")
            .website("website2")
            .contactEmail("contactEmail2")
            .contactPhone("contactPhone2")
            .defaultReservationDuration(2)
            .maxAdvanceBookingDays(2)
            .cancellationDeadlineHours(2);
    }

    public static Brand getBrandRandomSampleGenerator() {
        return new Brand()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .logoUrl(UUID.randomUUID().toString())
            .coverImageUrl(UUID.randomUUID().toString())
            .primaryColor(UUID.randomUUID().toString())
            .secondaryColor(UUID.randomUUID().toString())
            .website(UUID.randomUUID().toString())
            .contactEmail(UUID.randomUUID().toString())
            .contactPhone(UUID.randomUUID().toString())
            .defaultReservationDuration(intCount.incrementAndGet())
            .maxAdvanceBookingDays(intCount.incrementAndGet())
            .cancellationDeadlineHours(intCount.incrementAndGet());
    }
}
