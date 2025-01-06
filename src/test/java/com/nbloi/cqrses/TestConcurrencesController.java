package com.nbloi.cqrses;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nbloi.cqrses.command.aggregate.OrderAggregate;
import com.nbloi.cqrses.commonapi.dto.CreateOrderRequestDTO;
import com.nbloi.cqrses.commonapi.dto.CustomerDTO;
import com.nbloi.cqrses.commonapi.dto.OrderItemDTO;
import com.nbloi.cqrses.commonapi.dto.ProductDTO;
import com.nbloi.cqrses.commonapi.enums.CustomerStatus;
import com.nbloi.cqrses.commonapi.enums.OrderStatus;
import com.nbloi.cqrses.commonapi.enums.ProductStatus;
import com.nbloi.cqrses.commonapi.event.customer.CustomerDeletedEvent;
import com.nbloi.cqrses.commonapi.event.customer.CustomerUpdatedEvent;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.commonapi.query.customer.FindAllCustomersQuery;
import com.nbloi.cqrses.commonapi.query.customer.FindCustomerByIdQuery;
import com.nbloi.cqrses.commonapi.query.product.FindAllProductsQuery;
import com.nbloi.cqrses.commonapi.query.product.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.OrderItem;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.repository.CustomerRepository;
import com.nbloi.cqrses.query.repository.OrderRepository;
import com.nbloi.cqrses.query.repository.ProductRepository;
import com.nbloi.cqrses.query.service.CustomerEventHandler;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import com.nbloi.cqrses.query.service.ProductEventHandler;
import com.nbloi.cqrses.query.service.kafkaconsumer.orderconsumer.OrderProcessedEventConsumer;
import com.nbloi.cqrses.query.service.kafkaproducer.CustomerEventProducer;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestConcurrencesController {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private OrderEventHandler orderEventHandler;
    @Autowired
    private CustomerEventHandler customerEventHandler;

    // Implement the thread-safe executor
    private final ExecutorService executorService = Executors.newFixedThreadPool(30);
    @Autowired
    private ProductEventHandler productEventHandler;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerEventProducer customerEventProducer;
    @Autowired
    private OrderProcessedEventConsumer orderProcessedEventConsumer;
    @Autowired
    private ConsumerFactory consumerFactory;

    @Test
    public void contextLoads() {}

    private AggregateTestFixture<OrderAggregate> fixture;

    @BeforeEach
    public void setUp() {
        fixture = new AggregateTestFixture<>(OrderAggregate.class);
    }

    @Test
    @DisplayName("shall return OK when creating list of customers")
    public void testConcurrencesDataCompletenessAndValidityCreatingCustomers() throws Exception {
        String urlCustomerController = "/api/v1/customers/";

        // Read customers from Customer.json file
        List<CustomerDTO> customerList = getCustomerListFromJson();

        ResponseEntity customerResult = restTemplate.postForEntity("/api/v1/customers/create-listofcustomers",
                customerList,
                String.class);

        Thread.sleep(30000);

        Assertions.assertEquals(HttpStatus.CREATED, customerResult.getStatusCode());
        try {
            List<Customer> customerListRetrieved = customerEventHandler.handle(new FindAllCustomersQuery());

            // Test the uniqueness of data
            Assertions.assertEquals(customerList.size(), customerListRetrieved.size());
            for (Customer customer : customerListRetrieved) {
                // Test data completeness
                Assertions.assertNotNull(customer);

                // Test data validity
                Assertions.assertTrue(customer.getEmail().matches(".+@.+\\..+"));
                Assertions.assertNotNull(customer.getPhoneNumber(), () -> "Customer phone number is null");
                Assertions.assertTrue(customer.getBalance().compareTo(BigDecimal.ZERO) >= 0, () -> "Balance phone number is negative");

                // Test timeliness
                Assertions.assertTrue(customer.getCreatedAt().isBefore(LocalDateTime.now()), () -> "CreatedAt is later than now");
            }
            System.out.println("Response Body: " + customerResult.getBody());
            System.out.println("Response Status Code: " + customerResult.getStatusCode());

        } catch (Exception e) {
            Assertions.fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("shall return OK when creating list of products")
    public void testConcurrencesDataCompletenessAndValidityCreatingProducts() throws Exception {
        String urlCustomerController = "/api/v1/products/";

        // Read products from Product.json file
        List<ProductDTO> productList = getProductListFromJson();

        ResponseEntity productDTOListResult = restTemplate.postForEntity("/api/v1/products/create-listofproducts",
                productList,
                String.class);
        Thread.sleep(30000);
        List<Product> productListRetrieved = productEventHandler.handle(new FindAllProductsQuery());

                // Collect results
                Assertions.assertEquals(HttpStatus.CREATED, productDTOListResult.getStatusCode());

                // Test data completeness
                Assertions.assertEquals(productList.size(), productListRetrieved.size());
        try {
            for (Product product : productListRetrieved) {
                // // Test data completeness
                Assertions.assertNotNull(product);

                // Test data validity
                Assertions.assertTrue(product.getStock() >= 0, () -> "Stock should be greater than zero");
                Assertions.assertTrue(product.getPrice().compareTo(BigDecimal.ZERO) > 0, () -> "Price should be greater than zero");

            }
            System.out.println("Response: " + productDTOListResult.getBody());
            System.out.println("Response Status Code: " + productDTOListResult.getStatusCode());
        } catch (Exception e) {
            Assertions.fail("Exception occurred: " + e.getMessage());
        }
    }

    @DisplayName("should return OK when creating an order among 5 concurrences. Ensure data completeness and validity")
    @Test
    public void testConcurrencesDataUniquenessAndConsistencyOrderCreation() throws InterruptedException, IOException {
        // GIVEN
        // Alternatively change thread numbers based on the tests.
        int threadCount = 5;
//        int threadCount = 15;
//        int threadCount = 30;
        List<Callable<ResponseEntity>> tasks = new ArrayList<>();

        // Read customers from file Customer.json
        List<Customer> customerList = customerEventHandler.handle(new FindAllCustomersQuery());
        Customer requestedCustomers = customerList.get(10);
        String customerID = requestedCustomers.getCustomerId();
        BigDecimal initialBalance = requestedCustomers.getBalance();

        List<Product> productList = productEventHandler.handle(new FindAllProductsQuery());
        Product product1 = productList.get(0);
        String productId1 = product1.getProductId();
        int stock1 = product1.getStock();

        Product product2 = productList.get(1);
        String productId2 = product2.getProductId();
        int stock2 = product2.getStock();


        List<OrderItemDTO> orderItemList = new ArrayList<>();
        // create orderItem 1
        BigDecimal price1 = product1.getPrice();
        int quantity1 = 5;
        BigDecimal totalPrice1 = price1.multiply(BigDecimal.valueOf(quantity1));
        String currency = product1.getCurrency();
        OrderItemDTO oDTO1 = new OrderItemDTO(UUID.randomUUID().toString(), productId1, quantity1, price1, totalPrice1, currency);

        // create orderItem 2
        BigDecimal price2 = product2.getPrice();
        int quantity2 = 2;
        BigDecimal totalPrice2 = price2.multiply(BigDecimal.valueOf(quantity2));
        OrderItemDTO oDTO2 = new OrderItemDTO(UUID.randomUUID().toString(), productId2, quantity2, price2, totalPrice2, currency);
        // Save 2 orderItems into orderItemlist
        orderItemList.add(oDTO1);
        orderItemList.add(oDTO2);

        BigDecimal totalAmount = totalPrice1.add(totalPrice2);

        for (int i = 0; i < threadCount; i++) {
            int finalI = i;

            BigDecimal finalTotalAmount = totalAmount;
            tasks.add(() -> {
                CreateOrderRequestDTO orderRequestDTO = new CreateOrderRequestDTO(
                        orderItemList, finalTotalAmount, customerID, currency);
                System.out.println("Order created at thread number: " + finalI);

                long startTime = System.currentTimeMillis();
                System.out.println("Start time to create an order: " + startTime);
                ResponseEntity<String> orderCreatedResult = restTemplate.postForEntity("/api/v1/orders/create-order",
                        orderRequestDTO,
                        String.class);
                long endTime = System.currentTimeMillis();
                long createdDuration = endTime - startTime;
                System.out.println("End time to finish creating an order: " + endTime);
                System.out.println("Duration to completely create an order: " + createdDuration);
                Thread.sleep(90000);
                return orderCreatedResult;
            });
        }
//        executorService.shutdown();
//        executorService.awaitTermination(60, TimeUnit.SECONDS);

        try {
            // Invoke all tasks concurrently
            List<Future<ResponseEntity>> futures = executorService.invokeAll(tasks);

            List<Order> orderListCreated = new ArrayList<>();

            // Collect results
            for (Future<ResponseEntity> future : futures) {
                ResponseEntity<String> response = future.get();

//                String orderId = new ObjectMapper().writeValueAsString(response.getBody());
                String orderId = response.getBody();
                // Collect data from order created
                Order orderRetrieved = orderEventHandler.handle(new FindOrderByIdQuery(orderId));
                orderListCreated.add(orderRetrieved);
                Assertions.assertNotNull(orderRetrieved);
                Assertions.assertEquals(totalAmount, orderRetrieved.getTotalAmount());
                Assertions.assertEquals(currency, orderRetrieved.getCurrency());
                // Test consistency
                if (initialBalance.compareTo(totalAmount) >= 0) {
                    Assertions.assertEquals(OrderStatus.SHIPPED.toString(), orderRetrieved.getOrderStatus());
                } else if (initialBalance.compareTo(totalAmount) < 0) {
                    Assertions.assertEquals(OrderStatus.CREATED.toString(), orderRetrieved.getOrderStatus());
                }

                // Collect customer
                Customer customerRetrieved = orderRetrieved.getCustomer();
                // Test validity and completeness
                Assertions.assertNotNull(customerID, customerRetrieved.getCustomerId());
                Assertions.assertEquals(requestedCustomers.getName(), customerRetrieved.getName());
                Assertions.assertEquals(requestedCustomers.getEmail(), customerRetrieved.getEmail());
                Assertions.assertEquals(requestedCustomers.getPhoneNumber(), customerRetrieved.getPhoneNumber());
                Assertions.assertEquals(requestedCustomers.getCustomerStatus(), customerRetrieved.getCustomerStatus());
                Assertions.assertEquals(requestedCustomers.getCreatedAt(), customerRetrieved.getCreatedAt());

                Set<OrderItem> orderItemSet = orderRetrieved.getOrderItems();
                for (OrderItem orderItem : orderItemSet) {
                    Product productRetrieved = orderItem.getProduct();
                    // Test validity and completeness
                    if (productRetrieved.getProductId().equals(productId1)) {
                        Assertions.assertEquals(productId1, productRetrieved.getProductId());
                        Assertions.assertEquals(product1.getName(), productRetrieved.getName());
                        Assertions.assertEquals(product1.getPrice(), productRetrieved.getPrice());
                        Assertions.assertEquals(product1.getCurrency(), productRetrieved.getCurrency());
                    }
                    else if (productRetrieved.getProductId().equals(productId2)) {
                        Assertions.assertEquals(productId2, productRetrieved.getProductId());
                        Assertions.assertEquals(product2.getName(), productRetrieved.getName());
                        Assertions.assertEquals(product2.getPrice(), productRetrieved.getPrice());
                        Assertions.assertEquals(product2.getCurrency(), productRetrieved.getCurrency());
                    }
                }

                System.out.println("Response: " + response.getBody());
                Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
            }
            // Test uniqueness
            Assertions.assertEquals(futures.size(), orderListCreated.size());

            Customer customerJoined = customerEventHandler.handle(new FindCustomerByIdQuery(customerID));

            BigDecimal totalSpentOfAllThreads = totalAmount.multiply(BigDecimal.valueOf(threadCount));
            BigDecimal wantPostBalance = initialBalance.subtract(totalSpentOfAllThreads);
            Assertions.assertEquals(wantPostBalance, customerJoined.getBalance());

            int totalQuantity1 = quantity1 * threadCount;
            int totalQuantity2 = quantity2 * threadCount;

            Product productRetrieved1 = productEventHandler.handle(new FindProductByIdQuery(productId1));
            int wantPostStock1 = stock1 - totalQuantity1;
            Assertions.assertEquals(wantPostStock1, productRetrieved1.getStock());

            Product productRetrieved2 = productEventHandler.handle(new FindProductByIdQuery(productId2));
            int wantPostStock2 = stock2 - totalQuantity2;
            Assertions.assertEquals(wantPostStock2, productRetrieved2.getStock());

        } catch(Exception e){
            Assertions.fail("Exception occurred: " + e.getMessage());
        }

    }


    @DisplayName("should return status CREATED and catch the failed event when the event is failed. Then retry it to async with read model")
    @Test
    public void testConcurrencesDataConsistencyWhenRetryingFailedEventOrderCreation() throws InterruptedException, IOException {
        // Try to create an order with unknown product inside order item. Order Created Event will fail.
        // Then, create that product in database. And this event should be retried to keep on persisting the rest of operation in read model
        // GIVEN
        int threadCount = 5;
//        int threadCount = 15;
//        int threadCount = 30;
        List<Callable<ResponseEntity>> tasks = new ArrayList<>();

        // Read customers from file Customer.json
        List<Customer> customerList = customerEventHandler.handle(new FindAllCustomersQuery());
        Customer requestedCustomers = customerList.get(6);
        String customerID = requestedCustomers.getCustomerId();
        BigDecimal initialBalance = requestedCustomers.getBalance();

        // Read products persisted in database
        List<Product> productList = productEventHandler.handle(new FindAllProductsQuery());
        Product requestedProduct = productList.getFirst();
        String productId = requestedProduct.getProductId();
        BigDecimal price = requestedProduct.getPrice();
        int quantity = 5;
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity));
        String currency = requestedProduct.getCurrency();
        int stock = requestedProduct.getStock();

        // create orderItem with unknown product id
        List<OrderItemDTO> orderItemListUnknown = new ArrayList<>();
        OrderItemDTO oDTO = new OrderItemDTO(UUID.randomUUID().toString(), null, quantity, null, null, null);
        orderItemListUnknown.add(oDTO);

        // create orderItem with existing product id
        List<OrderItemDTO> orderItemList = new ArrayList<>();
        OrderItemDTO oDTO2 = new OrderItemDTO(UUID.randomUUID().toString(), productId, quantity, price, totalPrice, currency);
        orderItemList.add(oDTO2);
        LocalDateTime time;
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;

            BigDecimal finalTotalAmount = totalPrice;
            tasks.add(() -> {
                CreateOrderRequestDTO orderRequestDTO = new CreateOrderRequestDTO(
                        orderItemListUnknown, finalTotalAmount, customerID, currency);
                System.out.println("Order created at thread number: " + finalI);
                ResponseEntity<String> orderUnknownCreatedResult = restTemplate.postForEntity("/api/v1/orders/create-order",
                        orderRequestDTO, String.class);
                Thread.sleep(10000);
                System.out.println("Error of failed event when creating an unknown product order: " + orderRequestDTO);

                long startTimeToRetry = System.currentTimeMillis();
                System.out.println("Time to start retrying order created at time: " + startTimeToRetry + " at thread number: " + finalI);

                CreateOrderRequestDTO orderRequestDTO2 = new CreateOrderRequestDTO(
                        orderItemList, finalTotalAmount, customerID, currency);
                ResponseEntity<String> orderCreatedResult = restTemplate.postForEntity("/api/v1/orders/create-order",
                        orderRequestDTO2, String.class);

                long endTimeToRetry = System.currentTimeMillis();
                long retryCreatedOrderDuration = endTimeToRetry - startTimeToRetry;
                System.out.println("Time to end retrying order created at time: " + endTimeToRetry + " at thread number: " + finalI);
                System.out.println("After retrying, order created for duration: " + retryCreatedOrderDuration + " at thread number: " + finalI);

                long startTime = System.currentTimeMillis();
                System.out.println("Order created at time: " + startTime + " at thread number: " + finalI);

                System.out.println("The order has been created successfully after fix the failures and retry event with orderId: "
                        + orderCreatedResult.getBody());
                switch (threadCount) {
                    // Because handle the event failure requires more time. Please wait a minute to sync all related entities. Thanks
                    case 5:   Thread.sleep(60000);
                    case 15:   Thread.sleep(90000);
                    case 30:   Thread.sleep(120000);
                    default: Thread.sleep(60000);
                }
                return orderCreatedResult;
            });
        }

        try {
            // Invoke all tasks concurrently
            List<Future<ResponseEntity>> futures = executorService.invokeAll(tasks);

            List<Order> orderListCreated = new ArrayList<>();

            // Collect results
            for (Future<ResponseEntity> future : futures) {
                ResponseEntity<String> response = future.get();

                String orderId = response.getBody();
                // Collect data from order created
                Order orderRetrieved = orderEventHandler.handle(new FindOrderByIdQuery(orderId));
                orderListCreated.add(orderRetrieved);
                Assertions.assertNotNull(orderRetrieved);

                // Test customer
                Customer customerRetrieved = orderRetrieved.getCustomer();
                Assertions.assertNotNull(customerID, customerRetrieved.getCustomerId());
                Assertions.assertEquals(requestedCustomers.getName(), customerRetrieved.getName());
                Assertions.assertEquals(requestedCustomers.getEmail(), customerRetrieved.getEmail());
                Assertions.assertEquals(requestedCustomers.getPhoneNumber(), customerRetrieved.getPhoneNumber());
                Assertions.assertEquals(requestedCustomers.getCustomerStatus(), customerRetrieved.getCustomerStatus());
                Assertions.assertEquals(requestedCustomers.getCreatedAt(), customerRetrieved.getCreatedAt());

                Set<OrderItem> orderItemSet = orderRetrieved.getOrderItems();
                for (OrderItem orderItem : orderItemSet) {
                    Product productRetrieved = orderItem.getProduct();
                    if (productRetrieved.getProductId().equals(productId)) {
                        Assertions.assertEquals(productId, productRetrieved.getProductId());
                        Assertions.assertEquals(requestedProduct.getName(), productRetrieved.getName());
                        Assertions.assertEquals(requestedProduct.getPrice(), productRetrieved.getPrice());
                        Assertions.assertEquals(requestedProduct.getCurrency(), productRetrieved.getCurrency());
                    }
                }

                System.out.println("Response: " + response.getBody());
                Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
                long endTime = System.currentTimeMillis();
                System.out.println("The whole process of order created to sync all related entities ends at time: " + endTime);
            }
            Assertions.assertEquals(futures.size(), orderListCreated.size());

            Customer customerJoined = customerEventHandler.handle(new FindCustomerByIdQuery(customerID));

            BigDecimal totalSpent = totalPrice.multiply(BigDecimal.valueOf(threadCount));
            BigDecimal wantPostBalance = initialBalance.subtract(totalSpent);
            System.out.println("Want post balance: " + wantPostBalance);
            System.out.println("Actual post balance: " + customerJoined.getBalance());
            Assertions.assertEquals(wantPostBalance, customerJoined.getBalance());

            int totalQuantity = quantity * threadCount;

            Product productRetrieved1 = productEventHandler.handle(new FindProductByIdQuery(productId));
            int wantPostStock = stock - totalQuantity;
            Assertions.assertEquals(wantPostStock, productRetrieved1.getStock());

        } catch(Exception e){
            Assertions.fail("Exception occurred: " + e.getMessage());
        }
    }

    @DisplayName("should return status OK when updating the same customer simultaneously 5 concurrences")
    @Test
    public void testConcurrencesUpdateCustomerSimultaneously() throws Exception {
//                int threadCount = 5;
//        int threadCount = 15;
        int threadCount = 30;
        List<Callable<ResponseEntity<String>>> tasks = new ArrayList<>();

        List<Customer> customerList = customerEventHandler.handle(new FindAllCustomersQuery());
        Customer customerRequest = customerList.get(5);
        String customerId = customerRequest.getCustomerId();
        String name = customerRequest.getName();
        String email = customerRequest.getEmail();
        String phoneNumber = customerRequest.getPhoneNumber();
        BigDecimal balance = customerRequest.getBalance();
        LocalDateTime createdAt =  customerRequest.getCreatedAt();
        String customerStatus = customerRequest.getCustomerStatus();
        Set<Order> orderInitialSet = customerRequest.getOrders();


        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            String phoneNumberUpdated =  String.valueOf(Math.round((Math.random()* 10000000)*(Math.random()* 10000)));

            tasks.add(() -> {
                CustomerDTO customerDTO = new CustomerDTO(
                        customerId, name, email, phoneNumber, balance);
                // Automatically update phoneNumbers based on the number of threads
                customerDTO.setPhoneNumber(phoneNumberUpdated);

                System.out.println("Updated Phone number is: " + phoneNumberUpdated + " at thread number: " + finalI);
                System.out.println("Order created at thread number: " + finalI);
                HttpEntity<CustomerDTO> httpEntity = new HttpEntity<>(customerDTO);
                ResponseEntity<String> updatedCustomer = restTemplate.exchange("/api/v1/customers/update/"+customerId,
                        HttpMethod.PUT,
                        httpEntity,
                        String.class);
                switch (threadCount) {
                    case 5: Thread.sleep(25000);
                    case 15: Thread.sleep(45000);
                    case 30: Thread.sleep(60000);
                    default: Thread.sleep(60000);
                }
                return updatedCustomer;
            });
        }

        List<Future<ResponseEntity<String>>> futures = executorService.invokeAll(tasks);
        for (Future<ResponseEntity<String>> future : futures) {
            ResponseEntity<String> response = future.get();
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Map<String, Object> mapResponse = new HashMap<>();
            mapResponse = new ObjectMapper().readValue(response.getBody(), mapResponse.getClass());

            Customer customerResponse = objectMapper.convertValue(mapResponse, Customer.class);
            Assertions.assertEquals(customerId, customerResponse.getCustomerId());
            Assertions.assertNotNull(customerResponse.getPhoneNumber());
            Assertions.assertNotEquals(phoneNumber, customerResponse.getPhoneNumber());

            System.out.println("Response of updated customer: " + response.getBody());
        }
    }

    @DisplayName("should return status OK when updating the same customer multiple times with the same customer event")
    @Test
    public void testConcurrencesUpdateOneCustomerMultipleTimesWithManySameEvents() throws Exception {
//        int threadCount = 5;
//        int threadCount = 15;
        int threadCount = 30;
        List<Callable<ResponseEntity<String>>> tasks = new ArrayList<>();

        List<Customer> customerList = customerEventHandler.handle(new FindAllCustomersQuery());
        Customer customerRequest = customerList.get(15);
        String customerId = customerRequest.getCustomerId();

        String phoneNumberUpdated =  String.valueOf(Math.round((Math.random()* 10000000)*(Math.random()* 10000)));

        CustomerDeletedEvent customerDeletedEvent = new CustomerDeletedEvent(customerId);
        // Define deleted customer event id
        String deletedEventId = customerDeletedEvent.getCustomerDeletedEventId();

        String customerDeletedEventPayload = new ObjectMapper().writeValueAsString(customerDeletedEvent);
        // Send multiple times for unique customer deleted event
        customerEventProducer.sendCustomerDeletedEvent(customerDeletedEventPayload);
        customerEventProducer.sendCustomerDeletedEvent(customerDeletedEventPayload);
        customerEventProducer.sendCustomerDeletedEvent(customerDeletedEventPayload);

        Thread.sleep(15000);
        // Verify consumer processed the event only one getProcessedCount
        Assertions.assertEquals(1, orderProcessedEventConsumer.getProcessedCount(deletedEventId));
        System.out.println("Updated customer with id: " + customerId + " phone number is: " + phoneNumberUpdated);

//        for (int i = 0; i < threadCount; i++) {
//            int finalI = i;
//
//            tasks.add(() -> {
//
//
//                System.out.println("Updated Phone number is: " + phoneNumberUpdated + " at thread number: " + finalI);
//                System.out.println("Order created at thread number: " + finalI);
//
//                ResponseEntity<String> deletedCustomer = restTemplate.exchange("/api/v1/customers/delete/"+ customerId,
//                        HttpMethod.DELETE,
//                        HttpEntity.EMPTY,
//                        String.class);
//
//                return deletedCustomer;
//            });
//        }
//        // Wait for consumer to process events
//        Thread.sleep(5000);
//
//
//
//        List<Future<ResponseEntity<String>>> futures = executorService.invokeAll(tasks);
//        for (Future<ResponseEntity<String>> future : futures) {
//            ResponseEntity<String> response = future.get();
//            Assertions.assertNotNull(response);
//            Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//
//            String customerResponseId = response.getBody();
//            Customer customerResponse = customerEventHandler.handle(new FindCustomerByIdQuery(customerResponseId));
//
//            Assertions.assertNotNull(customerResponse);
//            Assertions.assertEquals(customerId, customerResponseId);
//            Assertions.assertEquals(CustomerStatus.DELETED.toString(), customerResponse.getCustomerStatus());
//            Assertions.assertTrue(customerResponse.getUpdatedAt().isAfter(LocalDateTime.now()));
//
//            System.out.println("Response of updated customer with same event: " + response.getBody());
//        }
    }

    @DisplayName("should return status OK when deleting a product should deleting all related-product orders")
    @Test
    public void testConcurrencesDeleteAProductLinkage() throws Exception {
        // GIVEN
        int threadCount = 5;
//        int threadCount = 15;
//        int threadCount = 30;
        List<Callable<ResponseEntity>> tasks = new ArrayList<>();

        // Read customers from file Customer.json
        List<Customer> customerList = customerEventHandler.handle(new FindAllCustomersQuery());
        Customer requestedCustomers = customerList.get(12);
        String customerID = requestedCustomers.getCustomerId();
        BigDecimal initialBalance = requestedCustomers.getBalance();

        List<Product> productList = productEventHandler.handle(new FindAllProductsQuery());
        Product product1 = productList.get(12);
        String productId1 = product1.getProductId();
        int stock1 = product1.getStock();

        List<OrderItemDTO> orderItemList = new ArrayList<>();
        // create orderItem 1
        BigDecimal price1 = product1.getPrice();
        int quantity1 = 5;
        BigDecimal totalPrice1 = price1.multiply(BigDecimal.valueOf(quantity1));
        String currency = product1.getCurrency();
        OrderItemDTO oDTO1 = new OrderItemDTO(UUID.randomUUID().toString(), productId1, quantity1, price1, totalPrice1, currency);

        // Save 2 orderItems into orderItemlist
        orderItemList.add(oDTO1);

        BigDecimal totalAmount = totalPrice1;

        for (int i = 0; i < threadCount; i++) {
            int finalI = i;

            BigDecimal finalTotalAmount = totalAmount;
            tasks.add(() -> {
                // Create an order
                CreateOrderRequestDTO orderRequestDTO = new CreateOrderRequestDTO(
                        orderItemList, finalTotalAmount, customerID, currency);
                System.out.println("Order created at thread number: " + finalI);
                ResponseEntity<String> orderCreatedResult = restTemplate.postForEntity("/api/v1/orders/create-order",
                        orderRequestDTO,
                        String.class);
                Thread.sleep(60000);

                // Delete the corresponding product
                ResponseEntity<String> deletedProductResult = restTemplate.exchange("/api/v1/products/delete/"+productId1,
                        HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        String.class);
                Thread.sleep(5000);

                // Read this order and should return err not found
                String orderId = orderCreatedResult.getBody();
                ResponseEntity<String> getDeletedOrder = restTemplate.getForEntity("/api/v1/orders/findbyid/"+orderId,
                        String.class);
                return getDeletedOrder;
            });
        }

        try {
            // Invoke all tasks concurrently
            List<Future<ResponseEntity>> futures = executorService.invokeAll(tasks);

            // Collect results
            for (Future<ResponseEntity> future : futures) {
                ResponseEntity<String> response = future.get();

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                Map<String, Object> mapResponse = new HashMap<>();
                mapResponse = new ObjectMapper().readValue(response.getBody(), mapResponse.getClass());

                Order order =  objectMapper.convertValue(mapResponse, Order.class);
                for (OrderItem orderItem : order.getOrderItems()) {
                    Product product = orderItem.getProduct();
                    Assertions.assertNotNull(product);
                    Assertions.assertEquals(product.getProductStatus(), ProductStatus.DELETED.toString());
                }

                // Collect data from order created
                Assertions.assertNotNull(response);
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

                System.out.println("Response: " + response.getBody());
            }
        } catch(Exception e){
            Assertions.fail("Exception occurred: " + e.getMessage());
        }
    }

    public List<CustomerDTO> getCustomerListFromJson() throws IOException {
        // Read customers from file Customer.json
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File("Customer.json"));
        JsonNode customerJson = jsonNode.get("customer");
        List<CustomerDTO> customerList = new ArrayList<>();
        for (JsonNode customerJsonNode : customerJson) {
            CustomerDTO customerDTO = new ObjectMapper().convertValue(customerJsonNode, CustomerDTO.class);
            customerList.add(customerDTO);
        }
        return customerList;
    }

    public List<ProductDTO> getProductListFromJson() throws IOException {
        // Read customers from file Product.json
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File("Product.json"));
        JsonNode productJson = jsonNode.get("product");
        List<ProductDTO> productList = new ArrayList<>();
        for (JsonNode productJsonNode : productJson) {
            ProductDTO productDTO = new ObjectMapper().convertValue(productJsonNode, ProductDTO.class);
            productList.add(productDTO);
        }
        return productList;
    }
}
