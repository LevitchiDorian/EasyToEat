package md.utm.restaurant.service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import md.utm.restaurant.domain.LocationHours;
import md.utm.restaurant.domain.Reservation;
import md.utm.restaurant.domain.RestaurantOrder;
import md.utm.restaurant.domain.enumeration.OrderStatus;
import md.utm.restaurant.domain.enumeration.ReservationStatus;
import md.utm.restaurant.repository.LocationHoursRepository;
import md.utm.restaurant.repository.ReservationRepository;
import md.utm.restaurant.repository.RestaurantOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scheduled service that auto-completes reservations once the location closes
 * or the reservation date has passed, and marks their orders as SERVED.
 */
@Service
public class ReservationAutoReleaseService {

    private static final Logger LOG = LoggerFactory.getLogger(ReservationAutoReleaseService.class);

    private static final List<ReservationStatus> ACTIVE_STATUSES = List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);

    private static final List<OrderStatus> ACTIVE_ORDER_STATUSES = List.of(OrderStatus.SUBMITTED, OrderStatus.PREPARING, OrderStatus.READY);

    private final ReservationRepository reservationRepository;
    private final RestaurantOrderRepository restaurantOrderRepository;
    private final LocationHoursRepository locationHoursRepository;
    private final FloorPlanNotificationService floorPlanNotificationService;
    private final OrderNotificationService orderNotificationService;

    public ReservationAutoReleaseService(
        ReservationRepository reservationRepository,
        RestaurantOrderRepository restaurantOrderRepository,
        LocationHoursRepository locationHoursRepository,
        FloorPlanNotificationService floorPlanNotificationService,
        OrderNotificationService orderNotificationService
    ) {
        this.reservationRepository = reservationRepository;
        this.restaurantOrderRepository = restaurantOrderRepository;
        this.locationHoursRepository = locationHoursRepository;
        this.floorPlanNotificationService = floorPlanNotificationService;
        this.orderNotificationService = orderNotificationService;
    }

    /**
     * Runs every 15 minutes.
     * 1. Completes all reservations from past dates.
     * 2. For today: completes reservations at locations whose closing time has passed.
     * 3. Marks all associated active orders as SERVED.
     * 4. Broadcasts WS updates.
     */
    @Scheduled(cron = "0 0/15 * * * ?")
    @Transactional
    public void autoReleaseExpiredReservations() {
        LocalDate today = LocalDate.now();
        DayOfWeek todayDow = today.getDayOfWeek();
        String nowHHMM = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        List<Reservation> toComplete = new ArrayList<>();

        // 1. Past-date reservations (clearly expired regardless of hours)
        toComplete.addAll(reservationRepository.findActiveBeforeDate(ACTIVE_STATUSES, today));

        // 2. Today's reservations — check if location's closing time has passed
        List<Reservation> todayActive = reservationRepository.findActiveOnDate(ACTIVE_STATUSES, today);
        if (!todayActive.isEmpty()) {
            Set<Long> locationIds = todayActive
                .stream()
                .filter(r -> r.getLocation() != null)
                .map(r -> r.getLocation().getId())
                .collect(Collectors.toSet());

            Map<Long, LocationHours> hoursMap = locationHoursRepository
                .findByLocationIdsAndDay(new ArrayList<>(locationIds), todayDow)
                .stream()
                .collect(Collectors.toMap(lh -> lh.getLocation().getId(), lh -> lh));

            for (Reservation res : todayActive) {
                Long locId = res.getLocation() != null ? res.getLocation().getId() : null;
                if (locId == null) continue;

                LocationHours lh = hoursMap.get(locId);
                if (lh == null) continue;

                boolean shouldClose =
                    Boolean.TRUE.equals(lh.getIsClosed()) || (lh.getCloseTime() != null && lh.getCloseTime().compareTo(nowHHMM) <= 0);

                if (shouldClose) toComplete.add(res);
            }
        }

        if (toComplete.isEmpty()) return;

        Set<Long> affectedLocations = new HashSet<>();

        for (Reservation res : toComplete) {
            res.setStatus(ReservationStatus.COMPLETED);
            res.setUpdatedAt(Instant.now());
            reservationRepository.save(res);

            // Mark linked active orders as SERVED
            for (RestaurantOrder order : res.getOrders()) {
                if (ACTIVE_ORDER_STATUSES.contains(order.getStatus())) {
                    order.setStatus(OrderStatus.SERVED);
                    order.setUpdatedAt(Instant.now());
                    restaurantOrderRepository.save(order);
                }
            }

            if (res.getLocation() != null) affectedLocations.add(res.getLocation().getId());
        }

        LOG.info("Auto-released {} reservations, affected locations: {}", toComplete.size(), affectedLocations);

        affectedLocations.forEach(locId -> {
            floorPlanNotificationService.notifyUpdate(locId);
            orderNotificationService.notifyOrderUpdate(locId);
        });
    }
}
