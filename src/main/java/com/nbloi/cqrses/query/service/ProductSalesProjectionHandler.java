package com.nbloi.cqrses.query.service;

import com.nbloi.cqrses.commonapi.event.OrderItemAddedEvent;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.entity.ProductSalesView;
import com.nbloi.cqrses.query.repository.ProductRepository;
import com.nbloi.cqrses.query.repository.ProductSalesViewRespository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Service
@Transactional
public class ProductSalesProjectionHandler {

    @Autowired
    private ProductSalesViewRespository productSalesViewRespository;

    @Autowired
    private ProductRepository productRepository;

    @EventHandler
    public void on(OrderItemAddedEvent event){
        Product product = productRepository.findById(event.getProductId()).get();

        ProductSalesView productSalesView = productSalesViewRespository.findById(event.getProductId()).
                orElse(new ProductSalesView(event.getProductId(), product.getName(), 0, 0.0));

        productSalesView.setTotalQuantitySold(productSalesView.getTotalQuantitySold() + event.getQuantity());
        productSalesView.setTotalRevenue(productSalesView.getTotalRevenue() + event.getTotalPrice().doubleValue());
        productSalesViewRespository.save(productSalesView);

    }
}
