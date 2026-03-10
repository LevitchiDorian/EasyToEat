package md.utm.restaurant.service;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
public class FloorPlanNotificationService {

    private final SimpMessageSendingOperations messagingTemplate;

    public FloorPlanNotificationService(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyUpdate(Long locationId) {
        if (locationId != null) {
            messagingTemplate.convertAndSend("/topic/floor-plan/" + locationId, "{\"updated\":true}");
        }
    }
}
