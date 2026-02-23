package md.utm.restaurant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RestaurantOrderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static RestaurantOrder getRestaurantOrderSample1() {
        return new RestaurantOrder().id(1L).orderCode("orderCode1");
    }

    public static RestaurantOrder getRestaurantOrderSample2() {
        return new RestaurantOrder().id(2L).orderCode("orderCode2");
    }

    public static RestaurantOrder getRestaurantOrderRandomSampleGenerator() {
        return new RestaurantOrder().id(longCount.incrementAndGet()).orderCode(UUID.randomUUID().toString());
    }
}
