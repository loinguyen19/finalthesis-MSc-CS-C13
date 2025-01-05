package com.nbloi.cqrses.commonapi.event.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nbloi.cqrses.commonapi.enums.EventType;
import com.nbloi.cqrses.commonapi.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ProductDeletedEvent {

    private String productId;
    private String productStatus= ProductStatus.DELETED.toString();
    private String productDeletedEventId;

    public ProductDeletedEvent(){}

    public ProductDeletedEvent(String productId) {
        this.productId = productId;
        this.productStatus = ProductStatus.DELETED.toString();
        this.productDeletedEventId = UUID.randomUUID().toString();
    }

    public String getProductId() {return productId;}
    public String getProductStatus() {return productStatus;}
}
