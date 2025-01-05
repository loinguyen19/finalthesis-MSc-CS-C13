package com.nbloi.cqrses.commonapi.query;

import com.nbloi.cqrses.query.entity.Customer;

public class FindOrderByCustomerQuery {

    private Customer customer;

    // Constructor
    public FindOrderByCustomerQuery() {
    }

    public FindOrderByCustomerQuery(Customer customer) {
        this.customer = customer;
    }


    public Customer getCustomer() {return customer;}

}
