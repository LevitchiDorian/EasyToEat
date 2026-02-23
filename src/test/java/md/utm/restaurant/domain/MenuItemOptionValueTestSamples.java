package md.utm.restaurant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MenuItemOptionValueTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MenuItemOptionValue getMenuItemOptionValueSample1() {
        return new MenuItemOptionValue().id(1L).label("label1").displayOrder(1);
    }

    public static MenuItemOptionValue getMenuItemOptionValueSample2() {
        return new MenuItemOptionValue().id(2L).label("label2").displayOrder(2);
    }

    public static MenuItemOptionValue getMenuItemOptionValueRandomSampleGenerator() {
        return new MenuItemOptionValue()
            .id(longCount.incrementAndGet())
            .label(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
