package com.nbloi.cqrses.query.service;

import com.nbloi.cqrses.commonapi.event.ProductInventoryEvent;
import com.nbloi.cqrses.commonapi.query.FindAllProductQuery;
import com.nbloi.cqrses.commonapi.query.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.repository.ProductRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class ProductInventoryEventHandler {

    @Autowired
    ProductRepository productRepository;

    public ProductInventoryEventHandler(ProductRepository productRepository) {
        super();
        this.productRepository = productRepository;
    }

    @EventHandler
    public void on(ProductInventoryEvent event) {
        Product product = productRepository.findById(event.getProductId()).get();

        product.setName(event.getName());
        product.setStock(event.getStock());
        product.setPrice(event.getPrice());
        product.setCurrency(event.getCurrency());
        productRepository.save(product);
    }

    @EventHandler
    public void off(ProductInventoryEvent event) {
        Product product = productRepository.findById(event.getProductId()).orElse(null);
        assert product != null;
        productRepository.delete(product);
    }

    @QueryHandler
    public List<Product> handle(FindAllProductQuery query) {
        return new ArrayList<>(productRepository.findAll());
    }

    @QueryHandler
    public Product handle(FindProductByIdQuery query) {
        return productRepository.findById(query.getProductById()).get();
    }
}

