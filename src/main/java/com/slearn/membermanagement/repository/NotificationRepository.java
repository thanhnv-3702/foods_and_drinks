package com.slearn.membermanagement.repository;

import com.slearn.membermanagement.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    List<Notification> findByRecipientIdAndReadOrderByCreatedAtDesc(Long recipientId, boolean read);

    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    Page<Notification> findByRecipientIdAndReadOrderByCreatedAtDesc(Long recipientId, boolean read, Pageable pageable);

    @Query("select count(n) from Notification n where n.recipient.id = :recipientId and n.read = false")
    long countUnreadByRecipientId(@Param("recipientId") Long recipientId);

    Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId);
}
