package com.nbloi.cqrses.query.repository;

import com.nbloi.cqrses.query.entity.ProductSalesView;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSalesViewRespository extends CrudRepository<ProductSalesView, String> {
}
