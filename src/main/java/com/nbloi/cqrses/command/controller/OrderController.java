package com.nbloi.cqrses.command.controller;

import com.nbloi.cqrses.commonapi.command.ConfirmOrderCommand;
import com.nbloi.cqrses.commonapi.command.CreateOrderCommand;
import com.nbloi.cqrses.commonapi.command.ShipOrderCommand;
import com.nbloi.cqrses.commonapi.dto.CreateOrderRequestDTO;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.query.FindAllOrderedProductsQuery;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.OrderDetails;
import com.nbloi.cqrses.query.service.kafkaproducer.OrderCreatedEventProducer;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/api/orders")
public class OrderController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final EventStore eventStore;

    @Autowired
    private OrderCreatedEventProducer orderCreatedEventProducer;
    @Autowired
    private ModelMapper modelMapper;

    // Autowiring constructor and POST/GET endpoints
    public OrderController(CommandGateway commandGateway, QueryGateway queryGateway, EventStore eventStore) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
        this.eventStore = eventStore;
    }

    @PostMapping("/create-order")
    public CompletableFuture<Void> createOrder(@RequestBody CreateOrderRequestDTO request) {
        String orderId = UUID.randomUUID().toString();
        CompletableFuture<Void> orderCreated = commandGateway.send(new CreateOrderCommand(orderId, request.getProductId()));

        // mapping between CreateOrderRequestDTO and CreateOrderEvent
        OrderCreatedEvent eventCreateOrder = modelMapper.map(request, OrderCreatedEvent.class);
        orderCreatedEventProducer.sendOrderEvent(eventCreateOrder);
        return orderCreated;
    }

    @PostMapping("/ship-order")
    public CompletableFuture<Void> shipOrder(@RequestBody CreateOrderRequestDTO request) {
        String orderId = UUID.randomUUID().toString();
        String productId = request.getProductId();
        return commandGateway.send(new CreateOrderCommand(orderId, productId))
                .thenCompose(result -> commandGateway.send(new ConfirmOrderCommand(orderId)))
                .thenCompose(result -> commandGateway.send(new ShipOrderCommand(orderId)));
    }

    @GetMapping("/all-orders")
    public CompletableFuture<List<OrderDetails>> findAllOrders() {
        return queryGateway.query(new FindAllOrderedProductsQuery(), ResponseTypes.multipleInstancesOf(OrderDetails.class));
    }

    @GetMapping("/findbyid/{orderId}")
    public CompletableFuture<List<OrderDetails>> findAllOrders(@PathVariable String orderId) {
        return queryGateway.query(new FindOrderByIdQuery(orderId), ResponseTypes.multipleInstancesOf(OrderDetails.class));
    }

    @GetMapping("/eventStore/{orderId}")
    public Stream eventStore(@PathVariable String orderId) {
        return eventStore.readEvents(orderId).asStream();
    }
}
