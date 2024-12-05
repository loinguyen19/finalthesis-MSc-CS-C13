package com.nbloi.cqrses.commonapi.query;

import com.nbloi.cqrses.commonapi.enums.OrderStatus;

public class FindOrderByIdQuery {

    private String orderItemId;

    // Constructor
    public FindOrderByIdQuery() {
    }

    public FindOrderByIdQuery(String orderItemId) {
        this.orderItemId = orderItemId;
    }


    public String getOrderId() {return orderItemId;}

}
