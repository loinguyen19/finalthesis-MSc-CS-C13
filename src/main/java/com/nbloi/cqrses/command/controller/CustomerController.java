package com.nbloi.cqrses.command.controller;

import com.nbloi.cqrses.commonapi.command.customer.CreateCustomerCommand;
import com.nbloi.cqrses.commonapi.dto.CustomerDTO;
import com.nbloi.cqrses.commonapi.event.customer.CustomerDeletedEvent;
import com.nbloi.cqrses.commonapi.event.customer.CustomerUpdatedEvent;
import com.nbloi.cqrses.commonapi.query.customer.FindAllCustomersQuery;
import com.nbloi.cqrses.commonapi.query.customer.FindCustomerByIdQuery;
import com.nbloi.cqrses.query.entity.Customer;
import com.nbloi.cqrses.query.service.CustomerEventHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.EventProcessingModule;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/customers")
public class CustomerController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final EventStore eventStore;

    @Autowired
    private EventProcessingModule eventProcessingModule;
    @Autowired
    private CustomerEventHandler customerEventHandler;


    // Autowiring constructor and POST/GET endpoints
    public CustomerController(CommandGateway commandGateway, QueryGateway queryGateway, EventStore eventStore) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
        this.eventStore = eventStore;
    }

    @PostMapping("/create-customer")
    public ResponseEntity createCustomer(@RequestBody CustomerDTO request) {
        String customerId = UUID.randomUUID().toString();
        String name = request.getName();
        String email = request.getEmail();
        String phoneNumber = request.getPhoneNumber();
        BigDecimal balance = request.getBalance();
        try {
            CompletableFuture<Void> customerCreated = commandGateway.send(new CreateCustomerCommand(customerId, name,
                    email, phoneNumber, balance));
            request.setCustomerId(customerId);
            return new ResponseEntity<>(request, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Your customer request can not be processed. Please review request payload",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-listofcustomers")
    public ResponseEntity createListOfCustomer(@RequestBody List<CustomerDTO> requestList) {
        try {
            List<CustomerDTO> customersCreatedList = new ArrayList<>();
            for (CustomerDTO request : requestList) {
                String customerId = UUID.randomUUID().toString();
                String name = request.getName();
                String email = request.getEmail();
                String phoneNumber = request.getPhoneNumber();
                BigDecimal balance = request.getBalance();
                LocalDateTime createdAt = LocalDateTime.now();

                CompletableFuture<Void> customerCreated = commandGateway.send(new CreateCustomerCommand(customerId, name,
                        email, phoneNumber, balance));
                if (customerCreated != null) {
                    request.setCustomerId(customerId);
                    customersCreatedList.add(request);
                }
            }
            return new ResponseEntity<>(customersCreatedList, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Your customer requests can not be processed. Please review request payload",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{customerId}")
    public ResponseEntity updateCustomer(@PathVariable String customerId, @Validated @RequestBody CustomerDTO customerToUpdate) {
        try {
            CustomerUpdatedEvent event = new CustomerUpdatedEvent(
                    customerId,
                    customerToUpdate.getName(),
                    customerToUpdate.getEmail(),
                    customerToUpdate.getPhoneNumber(),
                    customerToUpdate.getBalance()
            );
            customerEventHandler.on(event);
            Customer updatedCustomer = queryGateway.query(new FindCustomerByIdQuery(customerId), ResponseTypes.instanceOf(Customer.class)).join();
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(String.format("Customer with id: %s can not be found!!!", customerId), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all-customers")
    public ResponseEntity findAllCustomers() {
        try {
            List<Customer> customerList = queryGateway.query(new FindAllCustomersQuery(), ResponseTypes.multipleInstancesOf(Customer.class)).join();
            return new ResponseEntity<>(customerList, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(String.format("An error happened: %s", e.getMessage()), HttpStatus.OK);
        }
    }

    @GetMapping("/findbyid/{customerId}")
    public ResponseEntity findCustomerById(@PathVariable String customerId) {
        try {
            Customer customer = queryGateway.query(new FindCustomerByIdQuery(customerId), ResponseTypes.instanceOf(Customer.class)).join();
            return new ResponseEntity<Customer>(customer, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<String>(String.format("Customer with id: %s can not be found!!!", customerId), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/eventStore/{customerId}")
    public List<Object> eventStore(@PathVariable String customerId) {
        return eventStore.readEvents(customerId)
                .asStream() // Convert the event store into a stream
                .map(Message::getPayload) // Extract the payload of each event
                .collect(Collectors.toList()); // Convert them from stream into the list
    }

    @DeleteMapping("/delete/{customerId}")
    public ResponseEntity<String> deleteCustomer(@PathVariable String customerId) {
        try {
            CustomerDeletedEvent customerDeletedEvent = new CustomerDeletedEvent(customerId);
            customerEventHandler.delete(customerDeletedEvent);

            return new ResponseEntity<>(customerId, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format("Customer with id: %s can not be found to remove!!!", customerId), HttpStatus.NOT_FOUND);
        }
    }

    public boolean aggregateExists(String aggregateId) {
        return eventStore.readEvents(aggregateId).asStream().findAny().isPresent();
    }

}
