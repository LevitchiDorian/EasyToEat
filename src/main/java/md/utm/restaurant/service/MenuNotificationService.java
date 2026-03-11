package md.utm.restaurant.service;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
public class MenuNotificationService {

    private final SimpMessageSendingOperations messagingTemplate;

    public MenuNotificationService(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyMenuChange(Long brandId) {
        if (brandId != null) {
            messagingTemplate.convertAndSend("/topic/menu/" + brandId, "{\"updated\":true}");
        }
    }
}
