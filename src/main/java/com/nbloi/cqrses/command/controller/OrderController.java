package com.nbloi.cqrses.command.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.command.ConfirmOrderCommand;
import com.nbloi.cqrses.commonapi.command.CreateOrderCommand;
import com.nbloi.cqrses.commonapi.dto.CreateOrderRequestDTO;
import com.nbloi.cqrses.commonapi.dto.OrderDetailsDTO;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.query.FindAllOrderedProductsQuery;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.commonapi.query.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.OrderDetails;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.service.ProductInventoryEventHandler;
import com.nbloi.cqrses.query.service.kafkaproducer.OrderCreatedEventProducer;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.EventProcessingModule;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.util.ArrayList;
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
    private ModelMapper modelMapper;
    @Autowired
    private EventProcessingModule eventProcessingModule;

    @Autowired
    private ProductInventoryEventHandler productInventoryEventHandler;

    // Autowiring constructor and POST/GET endpoints
    public OrderController(CommandGateway commandGateway, QueryGateway queryGateway, EventStore eventStore) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
        this.eventStore = eventStore;
    }

    @PostMapping("/create-order")
    public CompletableFuture<Void> createOrder(@RequestBody CreateOrderRequestDTO request) throws IOException {
        String orderItemId = UUID.randomUUID().toString();

        Product productByIdQuery = productInventoryEventHandler.handle(new FindProductByIdQuery(request.getProductId()));
        int quantity = request.getQuantity();
        double amount = productByIdQuery.getPrice()*quantity;
        String currency = productByIdQuery.getCurrency();

        CompletableFuture<Void> orderCreated = commandGateway.send(new CreateOrderCommand(orderItemId, request.getProductId(),
                quantity, amount, currency));

//        // collect orderCreatedEvent and send to Kafka broker via producer
//        OrderCreatedEvent orderCreatedEventToConvert = new OrderCreatedEvent(orderItemId, request.getProductId(),
//                quantity, amount, currency);
//        OrderCreatedEvent eventCreateOrder = new ObjectMapper().convertValue(orderCreatedEventToConvert, OrderCreatedEvent.class);


        return orderCreated;
    }

    // we can not implement "confirm-order" on the interface because it should be done in the backend and after order being created and paid succesfully
    @PostMapping("/confirm-order/{orderItemId}")
    public String confirmOrder(@PathVariable String orderItemId) {
        commandGateway.send(new ConfirmOrderCommand(orderItemId));
        return "Order Id: " + orderItemId + " has been confirmed";
    }

//    // we can not implement "ship-order" on the interface because it should be done in the backend and after order being confirmed
//    @PostMapping("/ship-order")
//    public CompletableFuture<Void> shipOrder(@RequestBody CreateOrderRequestDTO request) {
//        String orderItemId = UUID.randomUUID().toString();
//        String productId = request.getProductId();
//        return commandGateway.send(new CreateOrderCommand(orderItemId, productId))
//                .thenCompose(result -> commandGateway.send(new ConfirmOrderCommand(orderItemId)))
//                .thenCompose(result -> commandGateway.send(new ShipOrderCommand(orderItemId)));
//    }

    @GetMapping("/all-orders")
    public CompletableFuture<List<OrderDetails>> findAllOrders() {
//        List<OrderDetails> listOrderDetails = queryGateway.query(new FindAllOrderedProductsQuery(),
//                ResponseTypes.multipleInstancesOf(OrderDetails.class)).join();
//
//        List<OrderDetailsDTO> listOrderDetailsDTO = new ArrayList<>();
//        for (OrderDetails orderDetails : listOrderDetails) {
//            listOrderDetailsDTO.add(modelMapper.map(orderDetails, OrderDetailsDTO.class));
//        }

        return queryGateway.query(new FindAllOrderedProductsQuery(), ResponseTypes.multipleInstancesOf(OrderDetails.class));
    }

    @GetMapping("/findbyid/{orderItemId}")
    public CompletableFuture<OrderDetails> findOrderById(@PathVariable String orderItemId) {
        return queryGateway.query(new FindOrderByIdQuery(orderItemId), ResponseTypes.instanceOf(OrderDetails.class));
    }


    @GetMapping("/eventStore/{orderItemId}")
    public Stream eventStore(@PathVariable String orderItemId) {
        return eventStore.readEvents(orderItemId).asStream();
    }
}
