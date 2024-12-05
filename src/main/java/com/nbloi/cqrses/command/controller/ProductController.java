package com.nbloi.cqrses.command.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.dto.ProductDTO;
import com.nbloi.cqrses.commonapi.event.ProductInventoryEvent;
import com.nbloi.cqrses.commonapi.query.FindAllProductQuery;
import com.nbloi.cqrses.commonapi.query.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.repository.ProductRepository;
import com.nbloi.cqrses.query.service.ProductInventoryEventHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final QueryGateway queryGateway;
    private final EventStore eventStore;

    @Autowired
    private final ModelMapper modelMapper;
    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private ProductInventoryEventHandler productInventoryEventHandler;
    @Autowired
    private ProductRepository productRepository;

    public ProductController(QueryGateway queryGateway, EventStore eventStore, ModelMapper modelMapper) {
        this.queryGateway = queryGateway;
        this.eventStore = eventStore;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/products")
    public List<ProductDTO> findAllProducts() {
        List<Product> listProduct = queryGateway.query(new FindAllProductQuery(),
                ResponseTypes.multipleInstancesOf(Product.class)).join();

        List<ProductDTO> listProductDTO = new ArrayList<>();
        for (Product product : listProduct){
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
            listProductDTO.add(productDTO);
        }
        return listProductDTO;
    }

    @GetMapping("/products/findbyid/{productId}")
    @ResponseBody
    public ProductDTO getProductById(@PathVariable String productId) {
        Product product = queryGateway.query(new FindProductByIdQuery(productId), ResponseTypes.instanceOf(Product.class))
                .join();
        return modelMapper.map(product, ProductDTO.class);
    }

    @GetMapping("/eventStore/{productId}")
    @ResponseBody
    public Stream eventStore(@PathVariable String productId) {
        return eventStore.readEvents(productId).asStream();
    }

    @PutMapping("/products/update/{productId}")
    public String updateProduct(@PathVariable String productId, @Validated @RequestBody ProductDTO request) {
        Product product = queryGateway.query(new FindProductByIdQuery(productId),
                ResponseTypes.instanceOf(Product.class)).join();
        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        ProductInventoryEvent productInventoryEvent = new ObjectMapper().convertValue(request, ProductInventoryEvent.class);

        commandGateway.send(new ProductInventoryEvent(productId, request.getName(),
                request.getStock(), request.getPrice(), request.getCurrency()));

        productInventoryEventHandler.on(productInventoryEvent);
        Product productUpdated = queryGateway.query(new FindProductByIdQuery(productId),
                ResponseTypes.instanceOf(Product.class)).join();

        return "Product with id: " + productId + " and " + " name: " + productUpdated.getName() + " was successfully updated";
    }

    @DeleteMapping("/products/delete/{productId}")
    public String deleteProduct(@PathVariable String productId) {
        Product product = queryGateway.query(new FindProductByIdQuery(productId),
                ResponseTypes.instanceOf(Product.class)).join();
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        ProductInventoryEvent productInventoryEvent = new ObjectMapper().convertValue(product, ProductInventoryEvent.class);
        productInventoryEventHandler.off(productInventoryEvent);
        return "Product with id: " + productId + " and name: " + product.getName() + " was successfully deleted";
    }
}
