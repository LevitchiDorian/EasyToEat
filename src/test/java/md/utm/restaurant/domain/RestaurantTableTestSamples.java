package md.utm.restaurant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RestaurantTableTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static RestaurantTable getRestaurantTableSample1() {
        return new RestaurantTable().id(1L).tableNumber("tableNumber1").minCapacity(1).maxCapacity(1).notes("notes1");
    }

    public static RestaurantTable getRestaurantTableSample2() {
        return new RestaurantTable().id(2L).tableNumber("tableNumber2").minCapacity(2).maxCapacity(2).notes("notes2");
    }

    public static RestaurantTable getRestaurantTableRandomSampleGenerator() {
        return new RestaurantTable()
            .id(longCount.incrementAndGet())
            .tableNumber(UUID.randomUUID().toString())
            .minCapacity(intCount.incrementAndGet())
            .maxCapacity(intCount.incrementAndGet())
            .notes(UUID.randomUUID().toString());
    }
}
