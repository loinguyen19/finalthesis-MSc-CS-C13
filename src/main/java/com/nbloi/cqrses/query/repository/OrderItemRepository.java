package com.nbloi.cqrses.query.repository;

import com.nbloi.cqrses.query.entity.OrderItem;
import com.nbloi.cqrses.query.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    List<OrderItem> findByProduct(Product product);
}
