package md.utm.restaurant.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class OrderItemOptionSelectionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static OrderItemOptionSelection getOrderItemOptionSelectionSample1() {
        return new OrderItemOptionSelection().id(1L).quantity(1);
    }

    public static OrderItemOptionSelection getOrderItemOptionSelectionSample2() {
        return new OrderItemOptionSelection().id(2L).quantity(2);
    }

    public static OrderItemOptionSelection getOrderItemOptionSelectionRandomSampleGenerator() {
        return new OrderItemOptionSelection().id(longCount.incrementAndGet()).quantity(intCount.incrementAndGet());
    }
}
