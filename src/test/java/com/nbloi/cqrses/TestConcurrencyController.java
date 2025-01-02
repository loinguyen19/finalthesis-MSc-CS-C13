package com.nbloi.cqrses;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.command.aggregate.CustomerAggregate;
import com.nbloi.cqrses.command.aggregate.OrderAggregate;
import com.nbloi.cqrses.commonapi.dto.CreateOrderRequestDTO;
import com.nbloi.cqrses.commonapi.dto.CustomerDTO;
import com.nbloi.cqrses.commonapi.dto.OrderItemDTO;
import com.nbloi.cqrses.commonapi.dto.ProductDTO;
import com.nbloi.cqrses.commonapi.enums.ProductStatus;
import com.nbloi.cqrses.commonapi.query.FindAllOrdersQuery;
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
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestConcurrencyController {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private OrderEventHandler orderEventHandler;
    @Autowired
    private CustomerEventHandler customerEventHandler;

    // Implement the thread-safe executor
    private final ExecutorService executorService = Executors.newFixedThreadPool(25);
    @Autowired
    private ProductEventHandler productEventHandler;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerAggregate customerAggregate;

    @Test
    public void contextLoads() {}

    private AggregateTestFixture<OrderAggregate> fixture;

    @BeforeEach
    public void setUp() {
        fixture = new AggregateTestFixture<>(OrderAggregate.class);
    }

    @Test
    @DisplayName("shall return OK when creating list of customers")
    public void testConcurrencyCreatingCustomers() throws Exception {
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
//            List<Customer> customerListRetrieved = customerRepository.findAll();
            Assertions.assertEquals(customerList.size(), customerListRetrieved.size());
            for (Customer customer : customerListRetrieved) {
                // Add assertions for completeness and validity
                Assertions.assertNotNull(customer);
                Assertions.assertTrue(customer.getEmail().matches(".+@.+\\..+"));
                Assertions.assertNotNull(customer.getPhoneNumber(), () -> "Customer phone number is null");
                Assertions.assertTrue(customer.getBalance().compareTo(BigDecimal.ZERO) >= 0, () -> "Balance phone number is negative");
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
    public void testConcurrencyCreatingProducts() throws Exception {
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
                Assertions.assertEquals(productList.size(), productListRetrieved.size());
        try {
            for (Product product : productListRetrieved) {
                // Add assertions for completeness and validity
                Assertions.assertNotNull(product);
                Assertions.assertTrue(product.getStock() >= 0, () -> "Stock should be greater than zero");
                Assertions.assertTrue(product.getPrice().compareTo(BigDecimal.ZERO) > 0, () -> "Price should be greater than zero");

            }
            System.out.println("Response: " + productDTOListResult.getBody());
            System.out.println("Response Status Code: " + productDTOListResult.getStatusCode());
        } catch (Exception e) {
            Assertions.fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testConcurrencyOrderCreation() throws InterruptedException, IOException {
        // GIVEN
        int threadCount = 5;
        List<Callable<ResponseEntity>> tasks = new ArrayList<>();

        // Read customers from file Customer.json
        List<Customer> customerList = customerEventHandler.handle(new FindAllCustomersQuery());
        long randomIndexCustomer = Math.round(Math.random()*customerList.size());
        Customer requestedCustomers = customerList.get((int) randomIndexCustomer);
        String customerID = requestedCustomers.getCustomerId();


        List<Product> productList = productEventHandler.handle(new FindAllProductsQuery());
        String productId1 = productList.get(0).getProductId();
        Product product1 = productEventHandler.handle(new FindProductByIdQuery(productId1));

        String productId2 = productList.get(1).getProductId();
        Product product2 = productEventHandler.handle(new FindProductByIdQuery(productId2));


        List<OrderItemDTO> orderItemList = new ArrayList<>();
        // create orderItem 1
        BigDecimal price1 = product1.getPrice();
        int quantity1 = 5;
        BigDecimal totalPrice1 = price1.multiply(BigDecimal.valueOf(quantity1));
        String currency = product1.getCurrency();
        OrderItemDTO oDTO1 = new OrderItemDTO(UUID.randomUUID().toString(), productId1, quantity1, price1, totalPrice1, currency);

        // create orderItem 2
        BigDecimal price2 = product2.getPrice();
        int quantity2 = 5;
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
                System.out.println("Order created at thread number: " + finalI );
                ResponseEntity<String> orderCreatedResult = restTemplate.postForEntity("/api/v1/orders/create-order",
                        orderRequestDTO,
                        String.class);

                Thread.sleep(45000);
                return orderCreatedResult;
            });
        }
//
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

                System.out.println("Response: " + response.getBody());
                Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
            }
                Assertions.assertEquals(futures.size(), orderListCreated.size());
        } catch(Exception e){
            Assertions.fail("Exception occurred: " + e.getMessage());
        }
    }

//    @Test
//    public

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
