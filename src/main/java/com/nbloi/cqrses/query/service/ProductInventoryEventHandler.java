package com.nbloi.cqrses.query.service;

import com.nbloi.cqrses.commonapi.query.FindAllProductQuery;
import com.nbloi.cqrses.commonapi.query.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.Products;
import com.nbloi.cqrses.query.repository.ProductRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class ProductInventoryEventHandler {

    @Autowired
    ProductRepository productRepository;

    public ProductInventoryEventHandler(ProductRepository productRepository) {
        super();
        this.productRepository = productRepository;
    }

    @QueryHandler
    public List<Products> findAllProducts(FindAllProductQuery query) {
        return new ArrayList<>(productRepository.findAll());
    }

    public Products findProductByIdQuery(FindProductByIdQuery query) {
        return productRepository.findById(query.getProductById()).get();
    }
}

