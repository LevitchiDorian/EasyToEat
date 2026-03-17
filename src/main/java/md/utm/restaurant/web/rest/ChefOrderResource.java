package md.utm.restaurant.web.rest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import md.utm.restaurant.domain.OrderItem;
import md.utm.restaurant.domain.Reservation;
import md.utm.restaurant.domain.ReservationTable;
import md.utm.restaurant.domain.RestaurantOrder;
import md.utm.restaurant.domain.UserProfile;
import md.utm.restaurant.domain.enumeration.OrderItemStatus;
import md.utm.restaurant.domain.enumeration.OrderStatus;
import md.utm.restaurant.repository.OrderItemRepository;
import md.utm.restaurant.repository.ReservationTableRepository;
import md.utm.restaurant.repository.RestaurantOrderRepository;
import md.utm.restaurant.repository.UserProfileRepository;
import md.utm.restaurant.security.SecurityUtils;
import md.utm.restaurant.service.OrderNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for the Chef Kitchen Display System.
 * GET  /api/chef/orders            — active orders for the chef's location
 * GET  /api/chef/orders/history    — served/cancelled orders by date
 * GET  /api/chef/orders/report     — aggregated totals (day/week/month)
 * PATCH /api/chef/orders/{id}/status — advance order status
 */
@RestController
@RequestMapping("/api/chef")
@PreAuthorize("isAuthenticated()")
public class ChefOrderResource {

    private final UserProfileRepository userProfileRepository;
    private final RestaurantOrderRepository restaurantOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReservationTableRepository reservationTableRepository;
    private final OrderNotificationService orderNotificationService;

    public ChefOrderResource(
        UserProfileRepository userProfileRepository,
        RestaurantOrderRepository restaurantOrderRepository,
        OrderItemRepository orderItemRepository,
        ReservationTableRepository reservationTableRepository,
        OrderNotificationService orderNotificationService
    ) {
        this.userProfileRepository = userProfileRepository;
        this.restaurantOrderRepository = restaurantOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.reservationTableRepository = reservationTableRepository;
        this.orderNotificationService = orderNotificationService;
    }

    private static final List<OrderStatus> ACTIVE_STATUSES = List.of(OrderStatus.SUBMITTED, OrderStatus.PREPARING, OrderStatus.READY);

    private static final List<OrderStatus> HISTORY_STATUSES = List.of(OrderStatus.SERVED, OrderStatus.CANCELLED);

    private static final List<OrderStatus> ALL_STATUSES = List.of(
        OrderStatus.SUBMITTED,
        OrderStatus.PREPARING,
        OrderStatus.READY,
        OrderStatus.SERVED,
        OrderStatus.CANCELLED
    );

    // ── Active orders (kitchen board) ──────────────────────────────────────────

    @GetMapping("/orders")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ChefOrderDTO>> getChefOrders() {
        Long locationId = resolveLocationId();

        List<RestaurantOrder> orders = locationId != null
            ? restaurantOrderRepository.findByLocationIdAndStatusIn(locationId, ACTIVE_STATUSES)
            : restaurantOrderRepository.findByStatusIn(ACTIVE_STATUSES);

        // Pre-orders: only show when the reservation is within the next 60 minutes (or already past)
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        orders = orders
            .stream()
            .filter(o -> {
                if (!Boolean.TRUE.equals(o.getIsPreOrder())) return true;
                Reservation res = o.getReservation();
                if (res == null || res.getReservationDate() == null || res.getStartTime() == null) return true;
                if (!res.getReservationDate().equals(today)) return false;
                LocalTime startTime = LocalTime.parse(
                    res.getStartTime().length() > 5 ? res.getStartTime().substring(0, 5) : res.getStartTime()
                );
                long minutesUntil = ChronoUnit.MINUTES.between(now, startTime);
                return minutesUntil <= 60; // show when ≤60 min away (including past-due)
            })
            .collect(Collectors.toList());

        // Sort ascending: pre-orders by reservation startTime, others by createdAt
        orders.sort(
            Comparator.comparing(o -> {
                Reservation res = o.getReservation();
                if (
                    Boolean.TRUE.equals(o.getIsPreOrder()) && res != null && res.getReservationDate() != null && res.getStartTime() != null
                ) {
                    return res.getReservationDate().toString() + "T" + res.getStartTime();
                }
                return o.getCreatedAt() != null ? o.getCreatedAt().toString() : "";
            })
        );

        return ResponseEntity.ok(toChefDTOs(orders));
    }

    // ── History (served / cancelled orders for a date) ─────────────────────────

    @GetMapping("/orders/history")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ChefOrderDTO>> getChefOrderHistory(@RequestParam(required = false) String date) {
        Long locationId = resolveLocationId();
        if (locationId == null) return ResponseEntity.ok(List.of());

        LocalDate target = date != null ? LocalDate.parse(date) : LocalDate.now();
        Instant from = target.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant to = target.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        List<RestaurantOrder> orders = restaurantOrderRepository.findByLocationAndStatusAndPeriod(locationId, HISTORY_STATUSES, from, to);

        return ResponseEntity.ok(toChefDTOs(orders));
    }

    // ── Report (aggregated totals) ─────────────────────────────────────────────

    @GetMapping("/orders/report")
    @Transactional(readOnly = true)
    public ResponseEntity<ReportDTO> getChefReport(@RequestParam(defaultValue = "day") String period) {
        Long locationId = resolveLocationId();
        if (locationId == null) return ResponseEntity.ok(new ReportDTO(0, 0, 0, 0.0, 0.0));

        LocalDate today = LocalDate.now();
        LocalDate from =
            switch (period) {
                case "week" -> today.minusDays(6);
                case "month" -> today.minusDays(29);
                default -> today;
            };

        Instant fromInstant = from.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant toInstant = today.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        List<RestaurantOrder> orders = restaurantOrderRepository.findByLocationAndStatusAndPeriod(
            locationId,
            ALL_STATUSES,
            fromInstant,
            toInstant
        );

        long total = orders.size();
        long served = orders.stream().filter(o -> o.getStatus() == OrderStatus.SERVED).count();
        long cancelled = orders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();
        double revenue = orders
            .stream()
            .filter(o -> o.getStatus() == OrderStatus.SERVED)
            .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount().doubleValue() : 0)
            .sum();
        double avg = served > 0 ? revenue / served : 0;

        return ResponseEntity.ok(new ReportDTO((int) total, (int) served, (int) cancelled, revenue, avg));
    }

    // ── Update status ──────────────────────────────────────────────────────────

    @PatchMapping("/orders/{id}/status")
    @Transactional
    public ResponseEntity<ChefOrderDTO> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(statusStr);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        RestaurantOrder order = restaurantOrderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();

        order.setStatus(newStatus);
        order.setUpdatedAt(Instant.now());
        restaurantOrderRepository.save(order);

        Long locationId = order.getLocation() != null ? order.getLocation().getId() : null;
        if (locationId != null) orderNotificationService.notifyOrderUpdate(locationId);

        String tableNumber = order.getTable() != null ? order.getTable().getTableNumber() : null;
        boolean isPreOrder = Boolean.TRUE.equals(order.getIsPreOrder());
        String resDate = (isPreOrder && order.getReservation() != null && order.getReservation().getReservationDate() != null)
            ? order.getReservation().getReservationDate().toString()
            : null;
        String resStart = (isPreOrder && order.getReservation() != null) ? order.getReservation().getStartTime() : null;
        return ResponseEntity.ok(
            new ChefOrderDTO(
                order.getId(),
                order.getOrderCode(),
                order.getStatus().name(),
                order.getCreatedAt() != null ? order.getCreatedAt().toString() : null,
                order.getUpdatedAt() != null ? order.getUpdatedAt().toString() : null,
                locationId,
                tableNumber,
                null,
                isPreOrder,
                resDate,
                resStart,
                List.of()
            )
        );
    }

    // ── Mark individual order item as ready ────────────────────────────────────

    @PatchMapping("/items/{itemId}/ready")
    @Transactional
    public ResponseEntity<Void> markItemReady(@PathVariable Long itemId) {
        OrderItem item = orderItemRepository.findById(itemId).orElse(null);
        if (item == null) return ResponseEntity.notFound().build();

        item.setStatus(OrderItemStatus.READY);
        orderItemRepository.save(item);

        Long locId = item.getOrder() != null && item.getOrder().getLocation() != null ? item.getOrder().getLocation().getId() : null;
        String table = null;
        if (item.getOrder() != null) {
            if (item.getOrder().getTable() != null) {
                table = item.getOrder().getTable().getTableNumber();
            } else if (item.getOrder().getReservation() != null) {
                List<ReservationTable> rts = reservationTableRepository.findByReservationId(item.getOrder().getReservation().getId());
                if (!rts.isEmpty() && rts.get(0).getTable() != null) {
                    table = rts.get(0).getTable().getTableNumber();
                }
            }
        }
        String name = item.getMenuItem() != null ? item.getMenuItem().getName() : null;
        orderNotificationService.notifyItemReady(locId, table, name);

        return ResponseEntity.ok().build();
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private List<ChefOrderDTO> toChefDTOs(List<RestaurantOrder> orders) {
        if (orders.isEmpty()) return List.of();

        List<Long> orderIds = orders.stream().map(RestaurantOrder::getId).toList();
        List<OrderItem> allItems = orderItemRepository.findByOrderIds(orderIds);

        Map<Long, List<OrderItem>> itemsByOrderId = allItems.stream().collect(Collectors.groupingBy(oi -> oi.getOrder().getId()));

        return orders
            .stream()
            .map(o -> {
                List<ChefItemDTO> items = itemsByOrderId
                    .getOrDefault(o.getId(), List.of())
                    .stream()
                    .map(oi ->
                        new ChefItemDTO(
                            oi.getId(),
                            oi.getQuantity(),
                            oi.getMenuItem() != null ? oi.getMenuItem().getName() : "—",
                            oi.getSpecialInstructions(),
                            oi.getStatus() != null ? oi.getStatus().name() : "PENDING"
                        )
                    )
                    .toList();

                Long locId = o.getLocation() != null ? o.getLocation().getId() : null;
                String tableNumber = o.getTable() != null ? o.getTable().getTableNumber() : null;
                if (tableNumber == null && o.getReservation() != null) {
                    List<ReservationTable> rts = reservationTableRepository.findByReservationId(o.getReservation().getId());
                    if (!rts.isEmpty() && rts.get(0).getTable() != null) {
                        tableNumber = rts.get(0).getTable().getTableNumber();
                    }
                }
                String reservationDate = null;
                String reservationStartTime = null;
                if (o.getReservation() != null) {
                    if (o.getReservation().getReservationDate() != null) reservationDate = o
                        .getReservation()
                        .getReservationDate()
                        .toString();
                    reservationStartTime = o.getReservation().getStartTime();
                }
                return new ChefOrderDTO(
                    o.getId(),
                    o.getOrderCode(),
                    o.getStatus().name(),
                    o.getCreatedAt() != null ? o.getCreatedAt().toString() : null,
                    o.getUpdatedAt() != null ? o.getUpdatedAt().toString() : null,
                    locId,
                    tableNumber,
                    null,
                    Boolean.TRUE.equals(o.getIsPreOrder()),
                    reservationDate,
                    reservationStartTime,
                    items
                );
            })
            .collect(Collectors.toCollection(ArrayList::new));
    }

    private Long resolveLocationId() {
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (login == null) return null;
        UserProfile profile = userProfileRepository.findByUserLogin(login).orElse(null);
        if (profile == null || profile.getLocation() == null) return null;
        return profile.getLocation().getId();
    }

    public record ChefOrderDTO(
        Long id,
        String orderCode,
        String status,
        String createdAt,
        String updatedAt,
        Long locationId,
        String tableNumber,
        String reservationCode,
        boolean isPreOrder,
        String reservationDate,
        String reservationStartTime,
        List<ChefItemDTO> items
    ) {}

    public record ChefItemDTO(Long id, Integer quantity, String menuItemName, String specialInstructions, String status) {}

    public record ReportDTO(int totalOrders, int servedOrders, int cancelledOrders, double totalRevenue, double avgOrderValue) {}
}
