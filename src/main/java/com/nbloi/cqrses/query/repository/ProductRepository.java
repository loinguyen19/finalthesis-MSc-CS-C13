package com.nbloi.cqrses.query.repository;

import com.nbloi.cqrses.query.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Query("SELECT p FROM Product p WHERE p.productStatus = 'ACTIVE' ")
    public List<Product> findAllActiveProduct();

    @Query("SELECT p FROM Product p WHERE p.productStatus = 'ACTIVE' AND p.productId= :productId ")
    public Product findActiveProductById(@Param("productId") String productId);
}
