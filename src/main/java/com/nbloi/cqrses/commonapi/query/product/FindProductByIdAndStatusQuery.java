package com.nbloi.cqrses.commonapi.query.product;

import com.nbloi.cqrses.commonapi.enums.ProductStatus;

public class FindProductByIdAndStatusQuery {

    private String productId;
    private String productStatus;

    public FindProductByIdAndStatusQuery(String productId, String productStatus) {
        this.productId = productId;
        this.productStatus = productStatus;
    }

    public FindProductByIdAndStatusQuery() {}

    public String getProductId() {return productId;}
    public String getProductStatus() {return productStatus;}

}
