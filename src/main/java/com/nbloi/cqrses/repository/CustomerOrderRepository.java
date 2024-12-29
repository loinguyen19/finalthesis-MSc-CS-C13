package com.nbloi.cqrses.query.repository;

import com.nbloi.cqrses.query.entity.CustomerOrderView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrderView, String> {

}
