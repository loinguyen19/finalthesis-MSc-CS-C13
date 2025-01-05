package com.nbloi.conventional.eda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.conventional.eda.dto.CreateOrderRequestDTO;
import com.nbloi.conventional.eda.dto.OrderItemDTO;
import com.nbloi.conventional.eda.event.OrderCreatedEvent;
import com.nbloi.conventional.eda.exception.OutOfProductStockException;
import com.nbloi.conventional.eda.exception.UnfoundEntityException;
import com.nbloi.conventional.eda.entity.Order;
import com.nbloi.conventional.eda.entity.OrderItem;
import com.nbloi.conventional.eda.entity.Product;
import com.nbloi.conventional.eda.service.OrderEventHandler;
import com.nbloi.conventional.eda.service.ProductEventHandler;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/eda/api/orders")
public class OrderController {

    @Autowired
    private OrderEventHandler orderEventHandler;
    @Autowired
    private ProductEventHandler productInventoryEventHandler;
    @Autowired
    private ModelMapper modelMapper;

    // Autowiring constructor and POST/GET endpoints
    public OrderController(OrderEventHandler orderEventHandler, ProductEventHandler productInventoryEventHandler) {
        this.orderEventHandler = orderEventHandler;
        this.productInventoryEventHandler = productInventoryEventHandler;
    }

    @PostMapping("/create-order")
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderRequestDTO request) {

            String orderId = UUID.randomUUID().toString();
//        String orderId = "c787751b-6ced-4912-a160-06ea55276a76";
            String customerId = request.getCustomerId();
            String paymentId = UUID.randomUUID().toString();

            List<OrderItemDTO> listOrderItemsDTO = request.getOrderItems();
            List<OrderItem> listOrderItems = new ArrayList<>();

            for (OrderItemDTO oDTO : listOrderItemsDTO) {
                Product productByIdQuery = productInventoryEventHandler.readProductById(oDTO.getProductId());

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
            orderEventHandler.on(new OrderCreatedEvent(orderId, listOrderItems, request.getTotalAmount(),
                    request.getCurrency(), customerId, paymentId));

        return new ResponseEntity<>(orderId, HttpStatus.CREATED);
    }

    @PostMapping("/create-listoforder")
    public ResponseEntity<List<CreateOrderRequestDTO>> createListOfOrder(@RequestBody CreateOrderRequestDTO []requestList) {
        List<CreateOrderRequestDTO> createOrderRequestDTOList = new ArrayList<>();
        for (CreateOrderRequestDTO request : requestList) {
            String orderId = UUID.randomUUID().toString();
            String customerId = request.getCustomerId();
            String paymentId = UUID.randomUUID().toString();

            List<OrderItemDTO> listOrderItemsDTO = request.getOrderItems();
            List<OrderItem> listOrderItems = new ArrayList<>();

            for (OrderItemDTO oDTO : listOrderItemsDTO) {
                Product productByIdQuery = productInventoryEventHandler.readProductById(oDTO.getProductId());

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
            orderEventHandler.on(new OrderCreatedEvent(orderId, listOrderItems,
                   request.getTotalAmount(), request.getCurrency(), customerId, paymentId));
            createOrderRequestDTOList.add(request);
        }
        return new ResponseEntity<>(createOrderRequestDTOList, HttpStatus.CREATED);
    }

    @GetMapping("/all-orders")
    public ResponseEntity<List<Order>> findAllOrders() {
        List<Order> listOrder = orderEventHandler.readAllOrders();
        return new ResponseEntity<>(listOrder, HttpStatus.OK);
    }

    @GetMapping("/findbyid/{orderId}")
    public ResponseEntity findOrderById(@PathVariable String orderId) {
        try {
            if (orderId == null || !IsValidUUID(orderId)) {
                return new ResponseEntity<>("Invalid uuid-formatted order id", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(orderEventHandler.readOrderById(orderId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Can not find order with id: " + orderId, HttpStatus.NOT_FOUND);
        }
    }

    public static boolean IsValidUUID (String uuid) {
        try {
            return UUID.fromString(uuid).toString() != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
