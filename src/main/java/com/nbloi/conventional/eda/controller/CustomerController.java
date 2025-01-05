package com.nbloi.conventional.eda.controller;

import com.nbloi.conventional.eda.dto.CustomerDTO;
import com.nbloi.conventional.eda.event.customer.CustomerCreatedEvent;
import com.nbloi.conventional.eda.event.customer.CustomerDeletedEvent;
import com.nbloi.conventional.eda.event.customer.CustomerUpdatedEvent;
import com.nbloi.conventional.eda.exception.UnfoundEntityException;
import com.nbloi.conventional.eda.entity.Customer;
import com.nbloi.conventional.eda.repository.CustomerRepository;
import com.nbloi.conventional.eda.service.CustomerEventHandler;
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
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/eda/api/customers")
public class CustomerController {

    @Autowired
    private CustomerEventHandler customerEventHandler;

    // Autowiring constructor and POST/GET endpoints
    public CustomerController(CustomerEventHandler customerEventHandler) {
        this.customerEventHandler = customerEventHandler;
    }

    @PostMapping("/create-customer")
    public ResponseEntity<String> createCustomer(@RequestBody CustomerDTO request) {
        String customerId = UUID.randomUUID().toString();
        String name = request.getName();
        String email = request.getEmail();
        String phoneNumber = request.getPhoneNumber();
        BigDecimal balance = request.getBalance();
        LocalDateTime createdAt = LocalDateTime.now();
        try {
            CustomerCreatedEvent customerCreatedEvent = new CustomerCreatedEvent(
                    customerId, name, email, phoneNumber, balance, createdAt);
            customerEventHandler.on(customerCreatedEvent);
            request.setCustomerId(customerId);
            return new ResponseEntity<>(customerId, HttpStatus.CREATED);

        } catch (UnfoundEntityException e) {
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

                CustomerCreatedEvent customerCreatedEvent = new CustomerCreatedEvent(
                        customerId, name, email, phoneNumber, balance, createdAt);

                customerEventHandler.on(customerCreatedEvent);
                request.setCustomerId(customerId);

                customersCreatedList.add(request);
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
            CustomerUpdatedEvent customerUpdatedEvent = new CustomerUpdatedEvent(
                    customerToUpdate.getCustomerId(),
                    customerToUpdate.getName(),
                    customerToUpdate.getEmail(),
                    customerToUpdate.getPhoneNumber(),
                    customerToUpdate.getBalance()
            );
            customerEventHandler.on(customerUpdatedEvent);
            return new ResponseEntity<>(customerUpdatedEvent, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format("Customer with id: %s can not be found!!!", customerId), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all-customers")
    public ResponseEntity findAllCustomers() {
        try {
            List<Customer> customerList = customerEventHandler.readAllCustomers();
            return new ResponseEntity<>(customerList, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(String.format("An error happened: %s", e.getMessage()), HttpStatus.OK);
        }
    }

    @GetMapping("/findbyid/{customerId}")
    public ResponseEntity findCustomerById(@PathVariable String customerId) {
        try {
            Customer customer = customerEventHandler.readCustomerById(customerId);
            return new ResponseEntity<Customer>(customer, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<String>(String.format("Customer with id: %s can not be found!!!", customerId), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{customerId}")
    public ResponseEntity<String> deleteCustomer(@PathVariable String customerId) {
        try {
            CustomerDeletedEvent customerDeletedEvent = new CustomerDeletedEvent(customerId);
            customerEventHandler.delete(customerDeletedEvent);
            return new ResponseEntity<>(customerId, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format("Customer with id: %s can not be found!!!",customerId), HttpStatus.NOT_FOUND);
        }
    }

}
