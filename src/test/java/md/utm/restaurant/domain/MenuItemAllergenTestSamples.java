package md.utm.restaurant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MenuItemAllergenTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MenuItemAllergen getMenuItemAllergenSample1() {
        return new MenuItemAllergen().id(1L).notes("notes1");
    }

    public static MenuItemAllergen getMenuItemAllergenSample2() {
        return new MenuItemAllergen().id(2L).notes("notes2");
    }

    public static MenuItemAllergen getMenuItemAllergenRandomSampleGenerator() {
        return new MenuItemAllergen().id(longCount.incrementAndGet()).notes(UUID.randomUUID().toString());
    }
}
