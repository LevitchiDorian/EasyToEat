package md.utm.restaurant.service;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

/**
 * WebSocket notification service for kitchen order updates.
 * Broadcasts to /topic/orders/{locationId} when an order status changes.
 */
@Service
public class OrderNotificationService {

    private final SimpMessageSendingOperations messagingTemplate;

    public OrderNotificationService(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyOrderUpdate(Long locationId) {
        if (locationId != null) {
            messagingTemplate.convertAndSend("/topic/orders/" + locationId, "{\"updated\":true}");
        }
    }

    public void notifyItemReady(Long locationId, String tableNumber, String itemName) {
        if (locationId == null) return;
        String safeTable = tableNumber != null ? tableNumber.replace("\"", "\\\"") : "";
        String safeName = itemName != null ? itemName.replace("\"", "\\\"") : "";
        messagingTemplate.convertAndSend(
            "/topic/orders/" + locationId,
            "{\"itemReady\":true,\"tableNumber\":\"" + safeTable + "\",\"itemName\":\"" + safeName + "\"}"
        );
    }
}
