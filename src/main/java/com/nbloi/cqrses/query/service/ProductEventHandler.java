package com.nbloi.cqrses.query.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.OutboxStatus;
import com.nbloi.cqrses.commonapi.enums.ProductStatus;
import com.nbloi.cqrses.commonapi.event.order.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.payment.PaymentFailedEvent;
import com.nbloi.cqrses.commonapi.event.product.ProductCreatedEvent;
import com.nbloi.cqrses.commonapi.event.product.ProductDeletedEvent;
import com.nbloi.cqrses.commonapi.event.product.ProductInventoryEvent;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.product.FindAllProductsQuery;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.commonapi.query.product.FindProductByIdAndStatusActiveQuery;
import com.nbloi.cqrses.commonapi.query.product.FindProductByIdAndStatusQuery;
import com.nbloi.cqrses.commonapi.query.product.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.OrderItem;
import com.nbloi.cqrses.query.entity.OutboxMessage;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.repository.OrderRepository;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Transactional
@Service
public class ProductEventHandler {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OutboxRepository outboxRepository;

    public ProductEventHandler(ProductRepository productRepository, OrderRepository orderRepository, OutboxRepository outboxRepository) {
        super();
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) {
        Product product = new Product(
                event.getProductId(),
                event.getName(),
                event.getPrice(),
                event.getStock(),
                event.getCurrency(),
                event.getProductStatus()
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
    public void delete(ProductDeletedEvent event) {
        try {
            Product product = productRepository.findById(event.getProductId()).orElse(null);
            if (product == null) {
                throw new UnfoundEntityException(event.getProductId(), Product.class.getName());
            }
            product.setProductStatus(ProductStatus.DELETED.toString());
            productRepository.save(product);

            // Save Outbox Message
            OutboxMessage outboxMessage = new OutboxMessage(
                    UUID.randomUUID().toString(),
                    event.getProductId(),
                    EventType.PRODUCT_DELETED_EVENT.toString(),
                    new ObjectMapper().writeValueAsString(event),
                    OutboxStatus.PENDING.toString());

            outboxRepository.save(outboxMessage);

        } catch (Exception e) {
            // Log the error for more specific message
            log.error("Error handling event: {}", event, e);
        }
    }

    @EventHandler
    public void revertProductBalance(PaymentFailedEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).orElse(null);
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
        Product product = productRepository.findById(query.getProductById()).orElse(null);
        if (product == null) {throw new UnfoundEntityException(query.getProductById(), Product.class.getName());}
        return product;
    }

    @QueryHandler
    public Product handle(FindProductByIdAndStatusQuery query) {
        Product product = productRepository.findByProductIdAndProductStatus(query.getProductId(), query.getProductStatus());
        if (product == null) {throw new UnfoundEntityException(query.getProductId(), Product.class.getName());}
        return product;
    }

    @QueryHandler
    public Product handle(FindProductByIdAndStatusActiveQuery query) {
        Product product = productRepository.findByProductIdAndProductStatus(query.getProductId(), ProductStatus.ACTIVE.toString());
        if (product == null) {throw new UnfoundEntityException(query.getProductId(), Product.class.getName());}
        return product;
    }
}

