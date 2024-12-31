package com.nbloi.cqrses.query.service;

import com.nbloi.cqrses.commonapi.event.PaymentFailedEvent;
import com.nbloi.cqrses.commonapi.event.ProductCreatedEvent;
import com.nbloi.cqrses.commonapi.event.ProductInventoryEvent;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.FindAllProductsQuery;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.commonapi.query.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.OrderItem;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.repository.OrderRepository;
import com.nbloi.cqrses.query.repository.ProductRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class ProductInventoryEventHandler {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderEventHandler orderEventHandler;

    public ProductInventoryEventHandler(ProductRepository productRepository) {
        super();
        this.productRepository = productRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) {
        Product product = new Product(
                event.getProductId(),
                event.getName(),
                event.getPrice(),
                event.getStock(),
                event.getCurrency()
        );

        productRepository.save(product);
    }

    @EventHandler
    public void on(ProductInventoryEvent event) {
        Product product = productRepository.findById(event.getProductId()).get();

        product.setName(event.getName());
        product.setPrice(event.getPrice());
        product.setStock(event.getStock());
        product.setCurrency(event.getCurrency());
        productRepository.save(product);
    }

    @EventHandler
    public void off(ProductInventoryEvent event) {
        Product product = productRepository.findById(event.getProductId()).orElse(null);
        if (product == null) {throw new UnfoundEntityException(event.getProductId(), Product.class.getName());}
        productRepository.delete(product);
    }

    @EventHandler
    public void revertProductBalance(PaymentFailedEvent event) {
        Order order = orderEventHandler.handle(new FindOrderByIdQuery(event.getOrderId()));
        if (order == null) {
            throw new UnfoundEntityException(event.getOrderId(), Order.class.getName());
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            String productId = orderItem.getProduct().getProductId();
            int quantity = orderItem.getQuantity();

            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                throw new UnfoundEntityException(productId, Product.class.getName());
            }
            product.setStock(product.getStock() + quantity);
            productRepository.save(product);
        }
    }

    @QueryHandler
    public List<Product> handle(FindAllProductsQuery query) {
        return new ArrayList<>(productRepository.findAll());
    }

    @QueryHandler
    public Product handle(FindProductByIdQuery query) {
        return productRepository.findById(query.getProductById()).get();
    }
}

