package com.nbloi.conventional.eda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.conventional.eda.dto.CreateOrderRequestDTO;
import com.nbloi.conventional.eda.dto.OrderItemDTO;
import com.nbloi.conventional.eda.event.OrderConfirmedEvent;
import com.nbloi.conventional.eda.event.OrderCreatedEvent;
import com.nbloi.conventional.eda.exception.OutOfProductStockException;
import com.nbloi.conventional.eda.exception.UnfoundEntityException;
import com.nbloi.conventional.eda.entity.Order;
import com.nbloi.conventional.eda.entity.OrderItem;
import com.nbloi.conventional.eda.entity.Product;
import com.nbloi.conventional.eda.service.OrderEventHandler;
import com.nbloi.conventional.eda.service.ProductInventoryEventHandler;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/eda/api/orders")
public class OrderController {

    @Autowired
    private OrderEventHandler orderEventHandler;
    @Autowired
    private ProductInventoryEventHandler productInventoryEventHandler;
    @Autowired
    private ModelMapper modelMapper;

    // Autowiring constructor and POST/GET endpoints
    public OrderController(OrderEventHandler orderEventHandler, ProductInventoryEventHandler productInventoryEventHandler) {
        this.orderEventHandler = orderEventHandler;
        this.productInventoryEventHandler = productInventoryEventHandler;
    }

    @PostMapping("/create-order")
    public List<CreateOrderRequestDTO> createOrder(@RequestBody CreateOrderRequestDTO []requestList) {
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
            Order orderCreated = orderEventHandler.on(new OrderCreatedEvent(orderId, listOrderItems,
                   request.getTotalAmount(), request.getCurrency(), customerId, paymentId));
            if (orderCreated != null) {
                createOrderRequestDTOList.add(request);
            }
        }
        return createOrderRequestDTOList;
    }

    @GetMapping("/all-orders")
    public List<CreateOrderRequestDTO> findAllOrders() {
        List<Order> listOrder = orderEventHandler.readAllOrders();
        List<CreateOrderRequestDTO> listCreateOrderRequestDTOList = new ArrayList<>();
        for (Order order : listOrder) {
            CreateOrderRequestDTO orderRequestDTO = modelMapper.map(order, CreateOrderRequestDTO.class);
            listCreateOrderRequestDTOList.add(orderRequestDTO);
        }
        return listCreateOrderRequestDTOList;
    }

    @GetMapping("/findbyid/{orderId}")
    public Order findOrderById(@PathVariable String orderId) {
        return orderEventHandler.readOrderById(orderId);
    }
}
