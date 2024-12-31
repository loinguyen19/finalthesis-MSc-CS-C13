package com.nbloi.cqrses.command.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.command.ConfirmOrderCommand;
import com.nbloi.cqrses.commonapi.command.CreateOrderCommand;
import com.nbloi.cqrses.commonapi.dto.CreateOrderRequestDTO;
import com.nbloi.cqrses.commonapi.dto.OrderItemDTO;
import com.nbloi.cqrses.commonapi.exception.OutOfProductStockException;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.FindAllOrdersQuery;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.commonapi.query.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.OrderItem;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.service.ProductInventoryEventHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.EventProcessingModule;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/orders")
public class OrderController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final EventStore eventStore;

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
    public ResponseEntity<CompletableFuture<Void>> createOrder(@RequestBody CreateOrderRequestDTO request) {
        try {
            String orderId = UUID.randomUUID().toString();
            String customerId = request.getCustomerId();
            String paymentId = UUID.randomUUID().toString();

            List<OrderItemDTO> listOrderItemsDTO = request.getOrderItems();
            List<OrderItem> listOrderItems = new ArrayList<>();

            for (OrderItemDTO oDTO : listOrderItemsDTO) {
                Product productByIdQuery = productInventoryEventHandler.handle(new FindProductByIdQuery(oDTO.getProductId()));

                if (productByIdQuery.equals(new Product())) {
                    throw new UnfoundEntityException(oDTO.getProductId(), "Product");
                }
                // Update the quantity of product by id
                else if (productByIdQuery.getStock() < oDTO.getQuantity()) {
                    throw new OutOfProductStockException();
                }

                // mapping between CreateOrderRequestDTO and Order
                OrderItem orderItem = new ObjectMapper().convertValue(oDTO, OrderItem.class);
                orderItem.setOrderItemId(UUID.randomUUID().toString());
                orderItem.setProduct(productByIdQuery);
                listOrderItems.add(orderItem);
            }
            CompletableFuture<Void> orderCreated = commandGateway.send(new CreateOrderCommand(orderId, listOrderItems,
                    request.getTotalAmount(), request.getCurrency(), customerId, paymentId));

            return new ResponseEntity<>(orderCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // we can not implement "confirm-order" on the interface because it should be done in the backend and after order being created and paid succesfully
    @PostMapping("/confirm-order/{orderId}")
    public String confirmOrder(@PathVariable String orderId) {
        commandGateway.send(new ConfirmOrderCommand(orderId));
        return "Order Id: " + orderId + " has been confirmed";
    }

//    // we can not implement "ship-order" on the interface because it should be done in the backend and after order being confirmed
//    @PostMapping("/ship-order")
//    public CompletableFuture<Void> shipOrder(@RequestBody CreateOrderRequestDTO request) {
//        String orderId = UUID.randomUUID().toString();
//        String productId = request.getProductId();
//        return commandGateway.send(new CreateOrderCommand(orderId, productId))
//                .thenCompose(result -> commandGateway.send(new ConfirmOrderCommand(orderId)))
//                .thenCompose(result -> commandGateway.send(new ShipOrderCommand(orderId)));
//    }

    @GetMapping("/all-orders")
    public CompletableFuture<List<Order>> findAllOrders() {
//        List<OrderDetails> listOrderDetails = queryGateway.query(new FindAllOrderedProductsQuery(),
//                ResponseTypes.multipleInstancesOf(OrderDetails.class)).join();
//
//        List<OrderDetailsDTO> listOrderDetailsDTO = new ArrayList<>();
//        for (OrderDetails orderDetails : listOrderDetails) {
//            listOrderDetailsDTO.add(modelMapper.map(orderDetails, OrderDetailsDTO.class));
//        }

        return queryGateway.query(new FindAllOrdersQuery(), ResponseTypes.multipleInstancesOf(Order.class));
    }

    @GetMapping("/findbyid/{orderId}")
    public CompletableFuture<Order> findOrderById(@PathVariable String orderId) {
        return queryGateway.query(new FindOrderByIdQuery(orderId), ResponseTypes.instanceOf(Order.class));
    }


    @GetMapping("/eventStore/{orderId}")
    public List<Object> eventStore(@PathVariable String orderId) {
        return eventStore.readEvents(orderId)
                .asStream() // Convert the event store into a stream
                .map(event -> event.getPayload()) // Extract the payload of each event
                .collect(Collectors.toList()); // Convert them from stream into the list
    }
}
