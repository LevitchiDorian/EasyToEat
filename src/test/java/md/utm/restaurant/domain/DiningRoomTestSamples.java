package md.utm.restaurant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DiningRoomTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static DiningRoom getDiningRoomSample1() {
        return new DiningRoom().id(1L).name("name1").description("description1").floor(1).capacity(1).floorPlanUrl("floorPlanUrl1");
    }

    public static DiningRoom getDiningRoomSample2() {
        return new DiningRoom().id(2L).name("name2").description("description2").floor(2).capacity(2).floorPlanUrl("floorPlanUrl2");
    }

    public static DiningRoom getDiningRoomRandomSampleGenerator() {
        return new DiningRoom()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .floor(intCount.incrementAndGet())
            .capacity(intCount.incrementAndGet())
            .floorPlanUrl(UUID.randomUUID().toString());
    }
}
