package com.washwise.repository;

import com.washwise.entity.Order;
import com.washwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);
    
    List<Order> findByStatusOrderByCreatedAtDesc(String status);
    
    List<Order> findByUserAndStatusOrderByCreatedAtDesc(User user, String status);
    
    List<Order> findAllByOrderByCreatedAtDesc();

    @Query("SELECT o FROM Order o WHERE o.scheduledDate BETWEEN :startDate AND :endDate ORDER BY o.scheduledDate ASC")
    List<Order> findByScheduledDateBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.createdAt < :date ORDER BY o.createdAt ASC")
    List<Order> findPendingOrdersOlderThan(@Param("date") LocalDateTime date);
}