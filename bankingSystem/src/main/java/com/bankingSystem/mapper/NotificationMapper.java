package com.bankingSystem.mapper;

import com.bankingSystem.DTO.NotificationDTO;
import com.bankingSystem.entity.Notification;

public class NotificationMapper {
    public static NotificationDTO toDTO(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(n.getNotificationId());
        dto.setUserId(n.getUserId());
        dto.setType(n.getType());
        dto.setMessage(n.getMessage());
        dto.setTimestamp(n.getTimestamp());
        dto.setStatus(n.getStatus());
        return dto;
    }
}
