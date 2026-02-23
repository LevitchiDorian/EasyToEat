package md.utm.restaurant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MenuItemOptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MenuItemOption getMenuItemOptionSample1() {
        return new MenuItemOption().id(1L).name("name1").maxSelections(1).displayOrder(1);
    }

    public static MenuItemOption getMenuItemOptionSample2() {
        return new MenuItemOption().id(2L).name("name2").maxSelections(2).displayOrder(2);
    }

    public static MenuItemOption getMenuItemOptionRandomSampleGenerator() {
        return new MenuItemOption()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .maxSelections(intCount.incrementAndGet())
            .displayOrder(intCount.incrementAndGet());
    }
}
