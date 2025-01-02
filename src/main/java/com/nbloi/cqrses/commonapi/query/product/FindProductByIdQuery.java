package com.nbloi.cqrses.commonapi.query.product;

public class FindProductByIdQuery {

    private String productId;

    public FindProductByIdQuery(String productId) {
        this.productId = productId;
    }

    public FindProductByIdQuery() {}

    public String getProductById() {return productId;}

}
