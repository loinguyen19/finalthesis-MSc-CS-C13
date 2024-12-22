package com.nbloi.cqrses.commonapi.query.materializedview.customerproductrating;

public class FindAllRatingsByCustomerIdQuery {


    private String customerId;

    public FindAllRatingsByCustomerIdQuery(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() {return customerId;}

}
