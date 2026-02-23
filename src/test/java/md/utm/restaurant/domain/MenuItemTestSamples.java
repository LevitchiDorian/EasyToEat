package md.utm.restaurant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MenuItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MenuItem getMenuItemSample1() {
        return new MenuItem()
            .id(1L)
            .name("name1")
            .preparationTimeMinutes(1)
            .calories(1)
            .imageUrl("imageUrl1")
            .spicyLevel(1)
            .displayOrder(1);
    }

    public static MenuItem getMenuItemSample2() {
        return new MenuItem()
            .id(2L)
            .name("name2")
            .preparationTimeMinutes(2)
            .calories(2)
            .imageUrl("imageUrl2")
            .spicyLevel(2)
            .displayOrder(2);
    }

    public static MenuItem getMenuItemRandomSampleGenerator() {
        return new MenuItem()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .preparationTimeMinutes(intCount.incrementAndGet())
            .calories(intCount.incrementAndGet())
            .imageUrl(UUID.randomUUID().toString())
            .spicyLevel(intCount.incrementAndGet())
            .displayOrder(intCount.incrementAndGet());
    }
}
