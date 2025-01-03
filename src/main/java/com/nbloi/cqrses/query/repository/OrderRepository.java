package com.nbloi.cqrses.query.repository;

import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

}
