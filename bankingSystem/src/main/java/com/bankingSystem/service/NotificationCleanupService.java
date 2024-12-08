package com.bankingSystem.service;

import com.bankingSystem.entity.Notification;
import com.bankingSystem.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationCleanupService {

    private final NotificationRepository notificationRepository;

    public NotificationCleanupService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Run once a day at midnight: 0 0 0 * * ?
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldNotifications() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        // We need a query to delete where timestamp < cutoff
        // Add a custom query method in repository or use a derived query if possible
        // For deleting old records, let's create a custom query in the repository:

        // We'll first find old notifications and then delete them:
        // Or add a method in NotificationRepository like:
        // @Modifying
        // @Query("DELETE FROM Notification n WHERE n.timestamp < :cutoff")
        // void deleteOldNotifications(LocalDateTime cutoff);

        notificationRepository.deleteOldNotifications(cutoff);
    }
}
