package com.nbloi.cqrses.commonapi.event.product;

import com.nbloi.cqrses.commonapi.enums.EventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDeletedEvent {

    private String productId;
    private String type;

    public ProductDeletedEvent(){}

    public ProductDeletedEvent(String productId, String type) {
        this.productId = productId;
        this.type = EventType.PRODUCT_DELETED_EVENT.toString();
    }
}
