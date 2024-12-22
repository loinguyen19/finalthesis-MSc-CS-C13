package com.nbloi.cqrses.query.repository;

import com.nbloi.cqrses.query.entity.CustomerProductRateView;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerProductRateRepository extends JpaRepository<CustomerProductRateView, String> {

    double findAverageRatingByProductId(@Param("productId") String productId);

    double findAverageRatingByCustomerId(@Param("customerId") String customerId);

    CustomerProductRateView findByCustomerIdAndProductId(String customerId, String productId);

    @Query("Select COALESCE(ROUND(AVG(r.rating),2),0) FROM CustomerProductRateView r " +
            "WHERE r.customerId = :customerId AND r.productId = :productId")
    double findAverageRatingByCustomerIdAndProductId(@Param ("customerId") String customerId, @Param ("productId") String productId);
}
