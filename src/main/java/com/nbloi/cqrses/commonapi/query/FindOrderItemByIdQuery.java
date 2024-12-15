package com.nbloi.cqrses.commonapi.query;

public class FindOrderItemByIdQuery {

    private String orderItemId;

    // Constructor
    public FindOrderItemByIdQuery() {
    }

    public FindOrderItemByIdQuery(String orderItemId) {
        this.orderItemId = orderItemId;
    }


    public String getOrderId() {return orderItemId;}

}
