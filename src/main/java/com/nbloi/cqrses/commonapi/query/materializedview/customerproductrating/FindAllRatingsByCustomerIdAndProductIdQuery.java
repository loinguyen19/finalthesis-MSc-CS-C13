package com.nbloi.cqrses.commonapi.query.materializedview.customerproductrating;

public class FindAllRatingsByCustomerIdAndProductIdQuery {


    private String customerId;
    private String productId;


    public FindAllRatingsByCustomerIdAndProductIdQuery(String customerId, String productId) {
        this.customerId = customerId;
        this.productId = productId;
    }

    public String getCustomerId() {return customerId;}

    public String getProductId() {return productId;}

}
