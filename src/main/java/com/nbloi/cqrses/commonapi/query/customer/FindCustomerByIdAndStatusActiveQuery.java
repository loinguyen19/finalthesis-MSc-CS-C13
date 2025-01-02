package com.nbloi.cqrses.commonapi.query.customer;

import com.nbloi.cqrses.commonapi.enums.CustomerStatus;

public class FindCustomerByIdAndStatusActiveQuery {

    private String customerId;
    private String customerStatus = CustomerStatus.ACTIVE.toString();

    public FindCustomerByIdAndStatusActiveQuery(String customerId) {
        this.customerId = customerId;
        this.customerStatus = CustomerStatus.ACTIVE.toString();
    }

    public FindCustomerByIdAndStatusActiveQuery() {}

    public String getCustomerId() {return customerId;}
    public String getCustomerStatus() {return customerStatus;}

}
