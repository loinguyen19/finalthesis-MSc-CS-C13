package com.nbloi.cqrses.query.service;

import com.nbloi.cqrses.commonapi.event.order.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.payment.PaymentFailedEvent;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.OrderItem;
import com.nbloi.cqrses.query.entity.ProductSalesView;
import com.nbloi.cqrses.query.repository.OrderRepository;
import com.nbloi.cqrses.query.repository.OutboxRepository;
import com.nbloi.cqrses.query.repository.ProductRepository;
import com.nbloi.cqrses.query.repository.ProductSalesViewRespository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class ProductSalesProjectionHandler {

    @Autowired
    private ProductSalesViewRespository productSalesViewRespository;
    @Autowired
    private OrderRepository orderRepository;

    public ProductSalesProjectionHandler(ProductSalesViewRespository productSalesViewRespository, OrderRepository orderRepository) {
        super();
        this.productSalesViewRespository = productSalesViewRespository;
        this.orderRepository = orderRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent event){
        for (OrderItem orderItem : event.getOrderItems()) {
            String productId = orderItem.getProduct().getProductId();
            String productName = orderItem.getProduct().getName();
            BigDecimal productPrice = orderItem.getProduct().getPrice();
            int quantity = orderItem.getQuantity();

            ProductSalesView productSalesView = productSalesViewRespository.findById(productId).
                    orElse(new ProductSalesView(productId, productName, 0, 0.0, null, null));

            productSalesView.setTotalQuantitySold(productSalesView.getTotalQuantitySold() + quantity);
            productSalesView.setTotalRevenue(productSalesView.getTotalRevenue() + productPrice.doubleValue());
            productSalesView.setCreatedAt(event.getCreatedAt());
            productSalesViewRespository.save(productSalesView);
        }
    }

    @EventHandler
    public void on(PaymentFailedEvent event){
        // Method to handle the failed payment case in consumer paymentfailedevent
        // then remove the line of sale created for the order and orderitem id

        if (event != null) {
            String orderId = event.getOrderId();
            Order order = orderRepository.findById(orderId).orElse(new Order());

            for (OrderItem orderItem : order.getOrderItems()) {
                String productId = orderItem.getProduct().getProductId();
                String productName = orderItem.getProduct().getName();
                int quantity = orderItem.getQuantity();
                BigDecimal productPrice = orderItem.getProduct().getPrice();

                ProductSalesView productSalesView = productSalesViewRespository.findById(productId).
                        orElse(new ProductSalesView(productId, productName, 0, 0.0, null, null));

                productSalesView.setTotalQuantitySold(productSalesView.getTotalQuantitySold() - quantity);
                productSalesView.setTotalRevenue(productSalesView.getTotalRevenue() - productPrice.doubleValue());
                productSalesView.setUpdatedAt(LocalDateTime.now());
                productSalesViewRespository.save(productSalesView);
            }
        }
    }

}
