package com.nbloi.conventional.eda.service;

import com.nbloi.conventional.eda.event.ProductInventoryEvent;
import com.nbloi.conventional.eda.entity.Product;
import com.nbloi.conventional.eda.repository.ProductRepository;
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


    public void on(ProductInventoryEvent event) {
        Product product = productRepository.findById(event.getProductId()).get();

        product.setName(event.getName());
        product.setPrice(event.getPrice());
        product.setStock(event.getStock());
        product.setCurrency(event.getCurrency());
        productRepository.save(product);
    }

    public void off(ProductInventoryEvent event) {
        Product product = productRepository.findById(event.getProductId()).orElse(null);
        assert product != null;
        productRepository.delete(product);
    }

    public List<Product> readAllProducts() {
        return new ArrayList<>(productRepository.findAll());
    }

    public Product readProductById(String productId) {
        return productRepository.findById(productId).get();
    }
}

