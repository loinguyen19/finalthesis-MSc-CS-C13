package com.nbloi.cqrses;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.axonframework.test.aggregate.AggregateTestFixture;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    @Test
    public void contextLoads() {}
    @Autowired
    private EntityManager entityManager;

    @Test
    void testPerformanceComparison() {
        // Simulate a complex join query in a conventional system
        long conventionalStartTime = System.currentTimeMillis();
        List<Object[]> result = entityManager.createQuery(
                "SELECT o, c FROM Order o JOIN Customer c ON o.customer.customerId = c.customerId WHERE c.name = :name"
        ).setParameter("name", "mizna waheedh").getResultList();
        long conventionalDuration = System.currentTimeMillis() - conventionalStartTime;

        // Simulate a materialized view query in CQRS
        long cqrsStartTime = System.currentTimeMillis();
        List<CustomerOrderView> customerOrders = customerOrderViewRepository.findAllByCustomerName("mizna waheedh");
        long cqrsDuration = System.currentTimeMillis() - cqrsStartTime;

        // Assert CQRS is faster
        Assertions.assertTrue(cqrsDuration < conventionalDuration);
        //TODO: add concurrences into this test

        System.out.println("Time to query a complex join query in a conventional system: " + conventionalDuration);
        System.out.println("Time to query a materialized view query in CQRS: " + cqrsDuration);
    }

}
