package com.nbloi.cqrses.query.repository;

import com.nbloi.cqrses.query.entity.PaymentSummaryView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentSummaryViewRepository extends JpaRepository<PaymentSummaryView, String> {
}
