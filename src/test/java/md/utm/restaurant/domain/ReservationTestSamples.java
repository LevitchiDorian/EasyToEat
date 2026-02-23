package md.utm.restaurant.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ReservationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Reservation getReservationSample1() {
        return new Reservation()
            .id(1L)
            .reservationCode("reservationCode1")
            .startTime("startTime1")
            .endTime("endTime1")
            .partySize(1)
            .cancellationReason("cancellationReason1");
    }

    public static Reservation getReservationSample2() {
        return new Reservation()
            .id(2L)
            .reservationCode("reservationCode2")
            .startTime("startTime2")
            .endTime("endTime2")
            .partySize(2)
            .cancellationReason("cancellationReason2");
    }

    public static Reservation getReservationRandomSampleGenerator() {
        return new Reservation()
            .id(longCount.incrementAndGet())
            .reservationCode(UUID.randomUUID().toString())
            .startTime(UUID.randomUUID().toString())
            .endTime(UUID.randomUUID().toString())
            .partySize(intCount.incrementAndGet())
            .cancellationReason(UUID.randomUUID().toString());
    }
}
