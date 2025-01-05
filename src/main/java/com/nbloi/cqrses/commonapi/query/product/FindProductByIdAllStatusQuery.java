package com.nbloi.cqrses.commonapi.query.product;

public class FindProductByIdAllStatusQuery {

    private String productId;

    public FindProductByIdAllStatusQuery(String productId) {
        this.productId = productId;
    }

    public FindProductByIdAllStatusQuery() {}

    public String getProductById() {return productId;}

}
