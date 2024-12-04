package com.nbloi.cqrses.command.controller;

import com.nbloi.cqrses.commonapi.dto.ProductDTO;
import com.nbloi.cqrses.commonapi.query.FindOrderByIdQuery;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final QueryGateway queryGateway;
    private final EventStore eventStore;

    public ProductController(QueryGateway queryGateway, EventStore eventStore) {
        this.queryGateway = queryGateway;
        this.eventStore = eventStore;
    }

    @GetMapping("/product-inventory/{productId}")
    public CompletableFuture<List<ProductDTO>> getInventoryById(@PathVariable String productId) {
        return queryGateway.query(new FindOrderByIdQuery(productId), ResponseTypes.multipleInstancesOf(ProductDTO.class));
    }

    @GetMapping("/eventStore/{productId}")
    public Stream eventStore(@PathVariable String productId) {
        return eventStore.readEvents(productId).asStream();
    }
}
