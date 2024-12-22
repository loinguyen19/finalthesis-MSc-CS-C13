package com.nbloi.cqrses.commonapi.query.materializedview.customerproductrating;

public class FindAllRatingsByProductIdQuery {

    private String productId;

    public FindAllRatingsByProductIdQuery(String productId) {
        this.productId = productId;
    }

    public String getProductId() {return productId;}
}
