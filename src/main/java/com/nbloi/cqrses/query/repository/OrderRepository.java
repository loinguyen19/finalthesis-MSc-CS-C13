package com.nbloi.cqrses.query.repository;

import com.nbloi.cqrses.query.entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderDetails, String> {

}
