package com.nbloi.cqrses.commonapi.query;

public class FindOrderByCustomerIdQuery {

    private String customerId;

    // Constructor
    public FindOrderByCustomerIdQuery() {
    }

    public FindOrderByCustomerIdQuery(String customerId) {
        this.customerId = customerId;
    }


    public String getCustomerId() {return customerId;}

}
