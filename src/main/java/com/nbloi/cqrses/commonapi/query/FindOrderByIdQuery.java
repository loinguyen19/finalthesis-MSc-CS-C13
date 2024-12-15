package com.nbloi.cqrses.commonapi.query;

import com.nbloi.cqrses.commonapi.enums.OrderStatus;

public class FindOrderByIdQuery {

    private String orderId;

    // Constructor
    public FindOrderByIdQuery() {
    }

    public FindOrderByIdQuery(String orderId) {
        this.orderId = orderId;
    }


    public String getOrderId() {return orderId;}

}
