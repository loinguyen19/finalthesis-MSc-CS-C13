package com.nbloi.cqrses.commonapi.command;

import com.nbloi.cqrses.commonapi.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Getter
@Setter
public class DeleteProductCommand {

    @TargetAggregateIdentifier
    private String productId;
    private String productStatus;

    public DeleteProductCommand(String productId) {
        this.productId = productId;
        this.productStatus = ProductStatus.DELETED.toString();
    }

    public DeleteProductCommand() {}

}
