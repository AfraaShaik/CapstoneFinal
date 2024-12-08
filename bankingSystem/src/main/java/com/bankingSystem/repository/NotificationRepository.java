package com.bankingSystem.repository;

import com.bankingSystem.entity.Notification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserIdAndStatus(Long userId, String status);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.timestamp < :cutoff")
    void deleteOldNotifications(LocalDateTime cutoff);
}
