package com.slearn.membermanagement.repository;

import com.slearn.membermanagement.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    List<Notification> findByRecipientIdAndReadOrderByCreatedAtDesc(Long recipientId, boolean read);
}
