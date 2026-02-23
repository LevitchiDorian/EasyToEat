package md.utm.restaurant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ReservationTableTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ReservationTable getReservationTableSample1() {
        return new ReservationTable().id(1L).notes("notes1");
    }

    public static ReservationTable getReservationTableSample2() {
        return new ReservationTable().id(2L).notes("notes2");
    }

    public static ReservationTable getReservationTableRandomSampleGenerator() {
        return new ReservationTable().id(longCount.incrementAndGet()).notes(UUID.randomUUID().toString());
    }
}
