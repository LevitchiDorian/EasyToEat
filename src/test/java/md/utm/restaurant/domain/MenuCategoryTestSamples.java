package md.utm.restaurant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MenuCategoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MenuCategory getMenuCategorySample1() {
        return new MenuCategory().id(1L).name("name1").description("description1").imageUrl("imageUrl1").displayOrder(1);
    }

    public static MenuCategory getMenuCategorySample2() {
        return new MenuCategory().id(2L).name("name2").description("description2").imageUrl("imageUrl2").displayOrder(2);
    }

    public static MenuCategory getMenuCategoryRandomSampleGenerator() {
        return new MenuCategory()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .imageUrl(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
