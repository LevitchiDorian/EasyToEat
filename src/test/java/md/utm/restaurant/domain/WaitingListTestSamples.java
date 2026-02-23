package md.utm.restaurant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WaitingListTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static WaitingList getWaitingListSample1() {
        return new WaitingList().id(1L).requestedTime("requestedTime1").partySize(1).notes("notes1");
    }

    public static WaitingList getWaitingListSample2() {
        return new WaitingList().id(2L).requestedTime("requestedTime2").partySize(2).notes("notes2");
    }

    public static WaitingList getWaitingListRandomSampleGenerator() {
        return new WaitingList()
            .id(longCount.incrementAndGet())
            .requestedTime(UUID.randomUUID().toString())
            .partySize(intCount.incrementAndGet())
            .notes(UUID.randomUUID().toString());
    }
}
