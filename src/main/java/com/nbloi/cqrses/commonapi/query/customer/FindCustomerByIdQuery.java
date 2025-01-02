package com.nbloi.cqrses.commonapi.query.customer;

public class FindCustomerByIdQuery {

    private String customerId;

    // Constructor
    public FindCustomerByIdQuery() {
    }

    public FindCustomerByIdQuery(String customerId) {
        this.customerId = customerId;
    }


    public String getCustomerId() {return customerId;}

}
