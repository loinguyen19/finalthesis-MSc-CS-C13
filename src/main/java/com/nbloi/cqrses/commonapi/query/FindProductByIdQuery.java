package com.nbloi.cqrses.commonapi.query;

public class FindProductByIdQuery {

    private String productId;

    public FindProductByIdQuery(String productId) {
        this.productId = productId;
    }

    public FindProductByIdQuery() {}

    public String getProductById() {return productId;}

}
