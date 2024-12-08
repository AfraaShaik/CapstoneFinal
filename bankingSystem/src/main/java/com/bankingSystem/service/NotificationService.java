package com.bankingSystem.service;

import com.bankingSystem.DTO.NotificationDTO;
import com.bankingSystem.entity.Notification;
import com.bankingSystem.entity.NotificationPreference;
import com.bankingSystem.mapper.NotificationMapper;
import com.bankingSystem.repository.NotificationRepository;
import com.bankingSystem.repository.NotificationPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationPreferenceRepository preferenceRepository) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
    }

    @Transactional
    public NotificationDTO createNotification(Long userId, String type, String message) {
        // Check user preferences
        boolean allowed = isNotificationAllowed(userId, type);
        if (!allowed) {
            return null; // Preference disabled, do not create notification
        }

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        Notification saved = notificationRepository.save(notification);
        return NotificationMapper.toDTO(saved);
    }

    public List<NotificationDTO> getUserNotifications(Long userId, String status) {
        List<Notification> notifications;
        if (status != null && !status.isBlank()) {
            notifications = notificationRepository.findByUserIdAndStatus(userId, status.toUpperCase());
        } else {
            notifications = notificationRepository.findByUserId(userId);
        }

        return notifications.stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationDTO markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this notification");
        }

        notification.setStatus("READ");
        Notification updated = notificationRepository.save(notification);
        return NotificationMapper.toDTO(updated);
    }

    @Transactional
    public void updatePreference(Long userId, String preferenceKey, boolean enabled) {
        NotificationPreference preference = preferenceRepository.findByUserIdAndPreferenceKey(userId, preferenceKey)
                .orElseGet(() -> {
                    NotificationPreference np = new NotificationPreference();
                    np.setUserId(userId);
                    np.setPreferenceKey(preferenceKey);
                    return np;
                });
        preference.setEnabled(enabled);
        preferenceRepository.save(preference);
    }

    public boolean isNotificationAllowed(Long userId, String type) {
        return preferenceRepository.findByUserIdAndPreferenceKey(userId, type)
                .map(NotificationPreference::isEnabled)
                .orElse(true); // default to true if no preference set
    }
    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this notification");
        }

        notificationRepository.delete(notification);
    }

}
