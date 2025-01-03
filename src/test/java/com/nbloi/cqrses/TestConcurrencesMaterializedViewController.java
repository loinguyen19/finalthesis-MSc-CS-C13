package com.nbloi.cqrses;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nbloi.cqrses.command.aggregate.CustomerAggregate;
import com.nbloi.cqrses.command.aggregate.OrderAggregate;
import com.nbloi.cqrses.commonapi.dto.CreateOrderRequestDTO;
import com.nbloi.cqrses.commonapi.dto.CustomerDTO;
import com.nbloi.cqrses.commonapi.dto.OrderItemDTO;
import com.nbloi.cqrses.commonapi.dto.ProductDTO;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import com.nbloi.cqrses.commonapi.query.customer.FindAllCustomersQuery;
import com.nbloi.cqrses.commonapi.query.product.FindAllProductsQuery;
import com.nbloi.cqrses.commonapi.query.product.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.entity.CustomerOrderView;
import com.nbloi.cqrses.query.entity.Order;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.repository.CustomerOrderRepository;
import com.nbloi.cqrses.query.repository.CustomerRepository;
import com.nbloi.cqrses.query.repository.ProductRepository;
import com.nbloi.cqrses.query.service.CustomerEventHandler;
import com.nbloi.cqrses.query.service.OrderEventHandler;
import com.nbloi.cqrses.query.service.ProductEventHandler;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestConcurrencesMaterializedViewController {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private CustomerOrderRepository customerOrderViewRepository;

    // Implement the thread-safe executor
    private final ExecutorService executorService = Executors.newFixedThreadPool(25);
    @Autowired
    private DataSource dataSource;

    @Test
    public void contextLoads() {}
    @Autowired
    private EntityManager entityManager;

    @Test
    void testPerformanceComparison() throws InterruptedException, SQLException, ExecutionException {
        //TODO: Please run the test from TestConcurrencesController before running this Materialized View test
        // Make sure there is CustomerOrderProjection View in database when creating orders via tests
        int threadCount = 5;
        List<Callable<List<CustomerOrderView>>> tasksComplexJoin = new ArrayList<>();
        List<Callable<List<CustomerOrderView>>> taskView = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
//            executorService.submit(() -> {
            tasksComplexJoin.add(() -> {
                // Simulate a complex join query in a conventional system
                long conventionalStartTime = System.currentTimeMillis();
                Map<String, Object> params = new HashMap<>();
                String sql = "SELECT o.*, c.* FROM orders o JOIN customer c ON o.customer_id = c.customer_id WHERE c.name = :name";

                //TODO: double check this customer name if exists in database
                params.put("name", "vendan");
                NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
                List<Map<String, Object>> resultMap = template.queryForList(sql, params);

                List<CustomerOrderView> customerOrderComplexJoinList = new ArrayList<>();
                for (Map<String, Object> result : resultMap) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    CustomerOrderView customerOrderView = new CustomerOrderView();
                    customerOrderView.setCustomerOrderViewId(UUID.randomUUID().toString());
                    customerOrderView.setCustomerId(String.valueOf(result.get("customer_id")));
                    customerOrderView.setCustomerName(objectMapper.convertValue(result.get("name"), String.class));
                    customerOrderView.setOrderId(String.valueOf(result.get("order_id")));
                    customerOrderView.setOrderDate(objectMapper.convertValue(result.get("created_at"), LocalDateTime.class));
                    customerOrderView.setOrderStatus(String.valueOf(result.get("order_status")));
                    customerOrderView.setTotalOrderAmount(objectMapper.convertValue(result.get("total_amount"), BigDecimal.class)
                    );

                    customerOrderComplexJoinList.add(customerOrderView);
                }

                Assertions.assertNotNull(resultMap);
                long conventionalDuration = System.currentTimeMillis() - conventionalStartTime;

                // Simulate a materialized view query in CQRS
                long cqrsStartTime = System.currentTimeMillis();
                List<CustomerOrderView> customerOrdersView = customerOrderViewRepository.findAllByCustomerName("vendan");
                long cqrsDuration = System.currentTimeMillis() - cqrsStartTime;
                long deltaCRQSDurationAndConventionalDuration = conventionalDuration - cqrsStartTime;

                // Assert CQRS is faster
                Assertions.assertTrue(cqrsDuration < conventionalDuration);
                Assertions.assertNotNull(customerOrdersView);

                System.out.println("Customer Order View is faster than complex join customer and order with: " +
                        deltaCRQSDurationAndConventionalDuration + " seconds");

                // Check each query also go through all threads
                System.out.println("Complex join customer and order at thread number " + finalI);
                System.out.println("Customer Order View at thread number " + finalI);

                // Print out time to query
                System.out.println("Time to query for conventional duration of complex join: " + conventionalDuration + " at thread number" + finalI);
                System.out.println("Time to query for materialized Customer Order View duration: " + cqrsDuration + " at thread number" + finalI);

                return customerOrderComplexJoinList;
            });
        }

        List<Future<List<CustomerOrderView>>> futuresComplexJoin = executorService.invokeAll(tasksComplexJoin);

        for (Future<List<CustomerOrderView>> future: futuresComplexJoin){
            List<CustomerOrderView> responseList = future.get();
            for (CustomerOrderView response: responseList){
                System.out.println(response);
            }
        }

        // Process for Customer Order View
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
//            executorService.submit(() -> {
            taskView.add(() -> {
                // Simulate a materialized view query in CQRS
                long cqrsStartTime = System.currentTimeMillis();
                List<CustomerOrderView> customerOrdersView = customerOrderViewRepository.findAllByCustomerName("vendan");
                long cqrsDuration = System.currentTimeMillis() - cqrsStartTime;

                Assertions.assertNotNull(customerOrdersView);

                System.out.println("Customer Order View at thread number " + finalI);
                System.out.println("Time to query a  materialized Customer Order View duration: " + cqrsDuration + " at thread number" + finalI);

                return customerOrdersView;
            });
        }
        List<Future<List<CustomerOrderView>>> futuresView = executorService.invokeAll(taskView);

        for (int i=0; i < futuresView.size(); i++){
            List<CustomerOrderView> responseViewList = futuresView.get(i).get();
            List<CustomerOrderView> responseComplexJoinList = futuresComplexJoin.get(i).get();
            Assertions.assertEquals(responseViewList.size(), responseComplexJoinList.size());
            // Check if element is not null
            Assert.assertNotNull(responseViewList);
            Assert.assertNotNull(responseComplexJoinList);
        }

        Assertions.assertEquals(futuresComplexJoin.size(), futuresView.size());
        Assertions.assertEquals(futuresComplexJoin.size(), futuresView.size());

    }

    @Test
    void testSqlInjectionPrevention() {
        // Inject malicious input into the order retrieval endpoint
        String maliciousInput = "1 OR 1=1";
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/orders/findbyid/" + maliciousInput ,String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "SQL Injection should be prevented");
    }

}
