package com.bankingSystem.controller;

import com.bankingSystem.DTO.NotificationDTO;
import com.bankingSystem.service.NotificationService;
import com.bankingSystem.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")

public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getMyNotifications(
            @RequestParam(value = "status", required = false) String status,
            Authentication auth
    ) {
        User currentUser = (User) auth.getPrincipal();
        List<NotificationDTO> notifications = notificationService.getUserNotifications(currentUser.getUserId(), status);
        return ResponseEntity.ok(notifications);
    }


    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationDTO> markNotificationAsRead(
            @PathVariable Long notificationId, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        NotificationDTO updated = notificationService.markAsRead(currentUser.getUserId(), notificationId);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/preferences")
    public ResponseEntity<Void> updatePreferences(@RequestParam String type,
                                                  @RequestParam boolean enabled,
                                                  Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        notificationService.updatePreference(currentUser.getUserId(), type, enabled);
        return ResponseEntity.ok().build();
    }

    // For testing: create a notification manually
    @PostMapping("/test")
    public ResponseEntity<NotificationDTO> testCreateNotification(@RequestParam String type,
                                                                  @RequestParam String message,
                                                                  @RequestParam Long userId) {
        // This endpoint is just for testing. In production, notifications would be created by service logic.
        NotificationDTO dto = notificationService.createNotification(userId, type, message);
        if (dto == null) {
            return ResponseEntity.ok().build(); // means preference disabled
        }
        return ResponseEntity.ok(dto);
    }
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        notificationService.deleteNotification(currentUser.getUserId(), notificationId);
        return ResponseEntity.ok().build();
    }

}
