package com.nbloi.cqrses.commonapi.query.customer;

public class FindCustomerByIdAndStatusQuery {

    private String customerId;
    private String customerStatus;

    public FindCustomerByIdAndStatusQuery(String customerId, String customerStatus) {
        this.customerId = customerId;
        this.customerStatus = customerStatus;
    }

    public FindCustomerByIdAndStatusQuery() {}

    public String getCustomerId() {return customerId;}
    public String getCustomerStatus() {return customerStatus;}

}
