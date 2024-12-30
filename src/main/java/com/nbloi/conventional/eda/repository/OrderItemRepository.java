package com.nbloi.conventional.eda.repository;

import com.nbloi.conventional.eda.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

}
