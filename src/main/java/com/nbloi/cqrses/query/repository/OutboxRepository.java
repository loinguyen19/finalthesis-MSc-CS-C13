package com.nbloi.cqrses.query.repository;

import com.nbloi.cqrses.query.entity.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxMessage, String> {

    @Query("SELECT m FROM OutboxMessage m WHERE m.status = 'PENDING'")
    List<OutboxMessage> findPendingMessages();
}
