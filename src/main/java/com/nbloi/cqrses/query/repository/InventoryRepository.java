package com.nbloi.cqrses.query.repository;

import com.nbloi.cqrses.query.entity.InventoryDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<InventoryDetails, String> {
}
