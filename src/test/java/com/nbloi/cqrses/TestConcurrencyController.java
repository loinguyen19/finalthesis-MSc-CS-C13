package com.nbloi.cqrses;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.command.aggregate.OrderAggregate;
import com.nbloi.cqrses.commonapi.dto.CreateOrderRequestDTO;
import com.nbloi.cqrses.commonapi.dto.OrderItemDTO;
import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.commonapi.enums.SystemDefault;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import org.awaitility.Awaitility;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestConcurrencyController {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private OrderEventHandler orderEventHandler;

    // Implement the thread-safe executor
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    public void contextLoads() {}

    private AggregateTestFixture<OrderAggregate> fixture;

    @BeforeEach
    public void setUp() {
        fixture = new AggregateTestFixture<>(OrderAggregate.class);
    }

    @Test
    public void testConcurrencyOrderCreation() throws InterruptedException, IOException {
        // GIVEN
        // Number of threads to run concurrently
        int threadCount = 5;

        List<Callable<ResponseEntity>> tasks = new ArrayList<>();

        String orderItemId = UUID.randomUUID().toString();
        String orderItemId2 = UUID.randomUUID().toString();

        String productId = "UUID-10";
        String name = "Towel";
        BigDecimal price = BigDecimal.valueOf(30);
        int quantity1 = 10;
        int stock = 1000;
        String currency = "VND";

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File("Customer.json"));
        JsonNode customerJson = jsonNode.get("customer");
        List<Customer> customerList = new ArrayList<>();
        for (JsonNode customerJsonNode : customerJson) {
            Customer customer = new ObjectMapper().convertValue(customerJsonNode, Customer.class);
            customerList.add(customer);
        }

        String productId2 = "UUID-8";
        String name2 = "Desk";
        BigDecimal price2 = BigDecimal.valueOf(300);
        int quantity2 = 5;
        int stock2 = 780;

        Product product = new Product(productId, name, price, stock, currency);
        Product product2 = new Product(productId2, name2, price2, stock2, currency);

        OrderItemDTO orderItem = new OrderItemDTO(orderItemId, productId, quantity1, price, currency);
        OrderItemDTO orderItem2 = new OrderItemDTO(orderItemId2, productId2, quantity2, price2, currency);

        BigDecimal totalAmount = orderItem.getTotalPrice().add(orderItem2.getTotalPrice());

        List<OrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        orderItems.add(orderItem2);

        JsonNode orderItemJson = jsonNode.get("orderItem");
        for (JsonNode orderItemNode : orderItemJson) {
            OrderItemDTO orderItemDTO = new ObjectMapper().convertValue(orderItemNode, OrderItemDTO.class);
            orderItems.add(orderItemDTO);
            totalAmount = totalAmount.add(orderItemDTO.getTotalPrice());
        }

        String customerID = "UUID-C-1";
        String urlOrderController = "/api/v1/orders/";
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            BigDecimal finalTotalAmount = totalAmount;

            tasks.add(() -> {
                CreateOrderRequestDTO orderRequestDTO = new CreateOrderRequestDTO(
                        orderItems, finalTotalAmount, customerID, currency);
                System.out.println("Order created at thread number: " + finalI );
                ResponseEntity<String> orderCreated = restTemplate.postForEntity(urlOrderController+"create-order",
                        orderRequestDTO,
                        String.class);

                // Poll the projection state until it reflects the update
//                Awaitility.await()
//                        .atMost(15, TimeUnit.SECONDS)
//                        .until(() -> {
////                            Order order = orderEventHandler.handle(new FindOrderByIdQuery(orderCreated.getBody()));
////                            Thread.sleep(15000);
//                            return orderCreated.getBody() != null;
//                        });
                // Please wait 60 seconds to handle all events and persist data in database
                Thread.sleep(60000);
//                tasks.notify();
                return orderCreated;
            });
        }

        try {
            // Invoke all tasks concurrently
            List<Future<ResponseEntity>> futures = executorService.invokeAll(tasks);

            // Collect results
            for (Future<ResponseEntity> future : futures) {
                ResponseEntity<String> response = future.get();
                System.out.println("Response: " + response.getBody());
                Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
            }
        } catch (Exception e) {
            Assertions.fail("Exception occurred: " + e.getMessage());
        }
    }
}
