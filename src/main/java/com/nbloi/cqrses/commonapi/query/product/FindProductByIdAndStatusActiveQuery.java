package com.nbloi.cqrses.commonapi.query.product;

import com.nbloi.cqrses.commonapi.enums.ProductStatus;

public class FindProductByIdAndStatusActiveQuery {

    private String productId;
    private String productStatus = ProductStatus.ACTIVE.toString();

    public FindProductByIdAndStatusActiveQuery(String productId) {
        this.productId = productId;
        this.productStatus = ProductStatus.ACTIVE.toString();
    }

    public FindProductByIdAndStatusActiveQuery() {}

    public String getProductId() {return productId;}
    public String getProductStatus() {return productStatus;}

}
