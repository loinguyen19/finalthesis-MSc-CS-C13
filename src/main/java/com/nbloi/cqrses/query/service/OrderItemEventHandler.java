//package com.nbloi.cqrses.query.service;
//
//import com.nbloi.cqrses.commonapi.enums.OrderStatus;
//import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
//import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
//import com.nbloi.cqrses.commonapi.event.OrderItemCreatedEvent;
//import com.nbloi.cqrses.commonapi.event.OrderShippedEvent;
//import com.nbloi.cqrses.commonapi.exception.UnconfirmedOrderException;
//import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
//import com.nbloi.cqrses.commonapi.query.*;
//import com.nbloi.cqrses.message.MailGateway;
//import com.nbloi.cqrses.message.MailMessage;
//import com.nbloi.cqrses.query.entity.Order;
//import com.nbloi.cqrses.query.entity.OrderItem;
//import com.nbloi.cqrses.query.entity.Product;
//import com.nbloi.cqrses.query.repository.OrderItemRepository;
//import com.nbloi.cqrses.query.service.kafkaproducer.OrderCreatedEventProducer;
//import lombok.extern.slf4j.Slf4j;
//import org.axonframework.eventhandling.EventHandler;
//import org.axonframework.queryhandling.QueryHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@Transactional
//@Service
//@Slf4j
//public class OrderItemEventHandler {
//
//    @Autowired
//    private OrderItemRepository orderItemRepository;
//
//    @Autowired
//    private ProductInventoryEventHandler productInventoryEventHandler;
//
//    @Autowired
//    private final MailGateway mailGateway;
//
////    private static final Logger LOGGER = LoggerFactory.getLogger(OrderItemEventHandler.class);
//
//    // inject OrderCreatedEventProducer to send the event to Kafka broker
//    @Autowired
//    private OrderCreatedEventProducer orderCreatedEventProducer;
//
////    private final Map<String, OrderDetails> orders = new HashMap<>();
//
//    public OrderItemEventHandler(OrderItemRepository orderItemRepository, MailGateway mailGateway) {
//        super();
//        this.orderItemRepository = orderItemRepository;
//        this.mailGateway = mailGateway;
//    }
//
//    @EventHandler
//    public void on(OrderItemCreatedEvent event) {
//        String orderItemId = event.getOrderItemId();
//
//        OrderItem orderItemCreated = new OrderItem();
//        orderItemCreated.setOrderItemId(orderItemId);
////        orderItemCreated.setOrderCreatedStatus();
//        orderItemCreated.setQuantity(event.getQuantity());
//        orderItemCreated.setPrice(event.getPrice());
//        orderItemCreated.setTotalPrice(event.getTotalPrice());
//        orderItemCreated.setCurrency(event.getCurrency());
//
//        Product product = productInventoryEventHandler.handle(new FindProductByIdQuery(event.getProductId()));
//        if (product != null) {throw new UnfoundEntityException(event.getProductId(), "Product");}
//
//        orderItemCreated.setProduct(product);
//
////        Order order = orderItemCreated.getOrder();
////
////        if (order != null){
////            orderItemCreated.setOrder(order);
////        }
//
//        orderItemRepository.save(orderItemCreated);
//
//
//        // send message to outbox when write database already completes
//        MailMessage message = new MailMessage("Order item %s created".formatted(orderItemId),
//                "Your order item is marked as 'created' in our system and will be processed.",
//                "order item created");
//        log.info("Sending email for created order item {}", orderItemId);
//        mailGateway.sendMail(message);
//    }
//
//    // Event Handlers for OrderConfirmedEvent and OrderShippedEvent...
//    @QueryHandler
//    public List<OrderItem> handle(FindAllOrderItemsQuery query) {
//        return orderItemRepository.findAll();
//    }
//
//    @QueryHandler
//    public OrderItem handle(FindOrderItemByIdQuery query) {
//        return orderItemRepository.findById(query.getOrderId()).get();
//    }
//
//}