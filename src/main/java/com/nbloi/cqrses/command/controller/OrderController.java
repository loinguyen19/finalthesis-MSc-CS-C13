package com.nbloi.cqrses.command.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.command.ConfirmOrderCommand;
import com.nbloi.cqrses.commonapi.command.CreateOrderCommand;
import com.nbloi.cqrses.commonapi.dto.CreateOrderRequestDTO;
import com.nbloi.cqrses.commonapi.dto.OrderItemDTO;
import com.nbloi.cqrses.commonapi.exception.OutOfProductStockException;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.FindAllOrdersQuery;
import com.nbloi.cqrses.commonapi.query.FindOrderByCustomerQuery;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.commonapi.query.customer.FindCustomerByIdQuery;
import com.nbloi.cqrses.commonapi.query.product.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.OrderItem;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.service.ProductEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.EventProcessingModule;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/orders")
public class OrderController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final EventStore eventStore;

    @Autowired
    private EventProcessingModule eventProcessingModule;
    @Autowired
    private ProductEventHandler productInventoryEventHandler;

    // Autowiring constructor and POST/GET endpoints
    public OrderController(CommandGateway commandGateway, QueryGateway queryGateway, EventStore eventStore) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
        this.eventStore = eventStore;
    }

    @PostMapping(value = "/create-order", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderRequestDTO request) {
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

            return new ResponseEntity<>(orderId, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    // we can not implement "confirm-order" on the interface because it should be done in the backend and after order being created and paid succesfully
    @PostMapping("/confirm-order/{orderId}")
    public ResponseEntity<String> confirmOrder(@PathVariable String orderId) {
        try {
            if (orderId == null || IsValidUUID(orderId)) {
                return new ResponseEntity<>("Invalid uuid-formatted order id", HttpStatus.BAD_REQUEST);
            }
            commandGateway.send(new ConfirmOrderCommand(orderId));
            return new ResponseEntity<>("Order Id: " + orderId + " has been confirmed", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error parsing order Id " + orderId + " with UUID format.", HttpStatus.NOT_FOUND);
        }
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
        return queryGateway.query(new FindAllOrdersQuery(), ResponseTypes.multipleInstancesOf(Order.class));
    }

    @GetMapping("/findbyid/{orderId}")
    public ResponseEntity findOrderById(@PathVariable String orderId) {
        try {
            if (orderId == null || !IsValidUUID(orderId)) {
                return new ResponseEntity<>("Invalid uuid-formatted order id", HttpStatus.BAD_REQUEST);
            }
            commandGateway.send(new ConfirmOrderCommand(orderId));
            Order orderRetrieved = queryGateway.query(new FindOrderByIdQuery(orderId), ResponseTypes.instanceOf(Order.class)).join();
            return new ResponseEntity<>(orderRetrieved, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Can not find order with id: " + orderId, HttpStatus.NOT_FOUND);
        }
    }

//    @GetMapping("/findbycustomerid/{customerId}")
//    public CompletableFuture<Order> findOrderByCustomerId(@PathVariable String customerId) {
//        Customer customer = queryGateway.query(new FindCustomerByIdQuery(customerId), ResponseTypes.instanceOf(Customer.class)).join();
//        return queryGateway.query(new FindOrderByCustomerQuery(customer), ResponseTypes.instanceOf(Order.class));
//    }


    @GetMapping("/eventStore/{orderId}")
    public List<Object> eventStore(@PathVariable String orderId) {
        return eventStore.readEvents(orderId)
                .asStream() // Convert the event store into a stream
                .map(event -> event.getPayload()) // Extract the payload of each event
                .collect(Collectors.toList()); // Convert them from stream into the list
    }

    private static boolean IsValidUUID(String uuid) {
        try {
            return UUID.fromString(uuid).toString() != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
