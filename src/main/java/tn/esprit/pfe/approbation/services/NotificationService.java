package tn.esprit.pfe.approbation.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tn.esprit.pfe.approbation.entities.Notification;
import tn.esprit.pfe.approbation.repositories.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Notification createNotification(Notification notification) {
        notification.setTime(LocalDateTime.now().toString());
        Notification savedNotification = notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + notification.getUserId(), savedNotification);
        return savedNotification;
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByUserId(String userId) { // Add this
        return notificationRepository.findByUserId(userId);
    }

    public Notification updateNotification(Long id, Notification notification) {
        Notification existing = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        existing.setRead(notification.isRead());
        Notification updatedNotification = notificationRepository.save(existing);
        messagingTemplate.convertAndSend("/topic/notifications/" + existing.getUserId(), updatedNotification);
        return updatedNotification;
    }

    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        String userId = notification.getUserId();
        notificationRepository.deleteById(id);
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, "deleted:" + id);
    }

    public void markAllAsRead() {
        List<Notification> notifications = notificationRepository.findAll();
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
        // Optionally, notify each user individually if needed
        notifications.forEach(n -> messagingTemplate.convertAndSend("/topic/notifications/" + n.getUserId(), "markAllAsRead"));
    }

    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + notification.getUserId(), updatedNotification);
        return updatedNotification;
    }
}