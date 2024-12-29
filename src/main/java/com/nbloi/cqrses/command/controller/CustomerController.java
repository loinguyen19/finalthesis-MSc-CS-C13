package com.nbloi.cqrses.command.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.command.CreateCustomerCommand;
import com.nbloi.cqrses.commonapi.dto.CreateCustomerRequestDTO;
import com.nbloi.cqrses.commonapi.exception.OutOfProductStockException;
import com.nbloi.cqrses.commonapi.exception.UnfoundEntityException;
import com.nbloi.cqrses.commonapi.query.FindAllCustomersQuery;
import com.nbloi.cqrses.commonapi.query.FindCustomerByIdQuery;
import com.nbloi.cqrses.commonapi.query.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.service.ProductInventoryEventHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.EventProcessingModule;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/customers")
public class CustomerController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final EventStore eventStore;

    @Autowired
    private EventProcessingModule eventProcessingModule;


    // Autowiring constructor and POST/GET endpoints
    public CustomerController(CommandGateway commandGateway, QueryGateway queryGateway, EventStore eventStore) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
        this.eventStore = eventStore;
    }

    @PostMapping("/create-customer")
    public CompletableFuture<Void> createCustomer(@RequestBody CreateCustomerRequestDTO request) {
        String customerId = UUID.randomUUID().toString();
        String name = request.getName();
        String email = request.getEmail();
        String phoneNumber = request.getPhoneNumber();
        BigDecimal balance = request.getBalance();
        LocalDateTime createdAt = LocalDateTime.now();

        CompletableFuture<Void> customerCreated = commandGateway.send(new CreateCustomerCommand(customerId, name,
               email, phoneNumber, balance, createdAt));

        return customerCreated;
    }

    @PostMapping("/create-listofcustomers")
    public List<CreateCustomerRequestDTO> createListOfCustomer(@RequestBody CreateCustomerRequestDTO []requestList) {
        List<CreateCustomerRequestDTO> customersCreatedList = new ArrayList<>();
        for (CreateCustomerRequestDTO request : requestList) {
            String customerId = UUID.randomUUID().toString();
            String name = request.getName();
            String email = request.getEmail();
            String phoneNumber = request.getPhoneNumber();
            BigDecimal balance = request.getBalance();
            LocalDateTime createdAt = LocalDateTime.now();

            CompletableFuture<Void> customerCreated = commandGateway.send(new CreateCustomerCommand(customerId, name,
                    email, phoneNumber, balance, createdAt));
            if (customerCreated != null) {
                request.setCustomerId(customerId);
                customersCreatedList.add(request);
            }
        }
        return customersCreatedList;
    }


    @GetMapping("/all-customers")
    public CompletableFuture<List<Customer>> findAllCustomers() {
        return queryGateway.query(new FindAllCustomersQuery(), ResponseTypes.multipleInstancesOf(Customer.class));
    }

    @GetMapping("/findbyid/{customerId}")
    public CompletableFuture<Customer> findCustomerById(@PathVariable String customerId) {
        return queryGateway.query(new FindCustomerByIdQuery(customerId), ResponseTypes.instanceOf(Customer.class));
    }


    @GetMapping("/eventStore/{customerId}")
    public List<Object> eventStore(@PathVariable String customerId) {
        return eventStore.readEvents(customerId)
                .asStream() // Convert the event store into a stream
                .map(Message::getPayload) // Extract the payload of each event
                .collect(Collectors.toList()); // Convert them from stream into the list
    }
}
