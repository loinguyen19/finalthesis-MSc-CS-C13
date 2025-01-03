package com.nbloi.cqrses.command.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.cqrses.commonapi.command.CreateProductCommand;
import com.nbloi.cqrses.commonapi.command.DeleteProductCommand;
import com.nbloi.cqrses.commonapi.dto.ProductDTO;
import com.nbloi.cqrses.commonapi.event.product.ProductDeletedEvent;
import com.nbloi.cqrses.commonapi.event.product.ProductInventoryEvent;
import com.nbloi.cqrses.commonapi.query.product.FindAllProductsQuery;
import com.nbloi.cqrses.commonapi.query.product.FindProductByIdQuery;
import com.nbloi.cqrses.query.entity.Product;
import com.nbloi.cqrses.query.service.ProductEventHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final QueryGateway queryGateway;
    private final EventStore eventStore;

    @Autowired
    private final ModelMapper modelMapper;
    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private ProductEventHandler productInventoryEventHandler;

    public ProductController(QueryGateway queryGateway, EventStore eventStore, ModelMapper modelMapper) {
        this.queryGateway = queryGateway;
        this.eventStore = eventStore;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/create-product")
    public ResponseEntity<String> createProduct(@RequestBody ProductDTO request) {
        String productId = UUID.randomUUID().toString();
        String name = request.getName();
        BigDecimal price = request.getPrice();
        int stock = request.getStock();
        String currency = request.getCurrency();
        try {
            commandGateway.send(new CreateProductCommand(productId, name,
                    price, stock, currency));
            return new ResponseEntity<>(productId, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-listofproducts")
    public ResponseEntity createListOfProduct(@RequestBody List<ProductDTO> requestList) {
        try {
            List<ProductDTO> productsCreatedList = new ArrayList<>();
            for (ProductDTO request : requestList) {
                String productId = UUID.randomUUID().toString();
                String name = request.getName();
                BigDecimal price = request.getPrice();
                int stock = request.getStock();
                String currency = request.getCurrency();

                CompletableFuture<Void> productCreated = commandGateway.send(new CreateProductCommand(productId, name,
                        price, stock, currency));
                if (productCreated != null) {
                    request.setProductId(productId);
                    productsCreatedList.add(request);
                }
            }
            return new ResponseEntity<>(productsCreatedList, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Your product requests can not be processed. Please review request payload",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all-products")
    public ResponseEntity findAllProducts() {
        try {
            List<Product> listProduct = queryGateway.query(new FindAllProductsQuery(),
                    ResponseTypes.multipleInstancesOf(Product.class)).join();

            List<ProductDTO> listProductDTO = new ArrayList<>();
            for (Product product : listProduct) {
                ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                listProductDTO.add(productDTO);
            }
            return new ResponseEntity<>(listProductDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format("An error happened: %s", e.getMessage()), HttpStatus.OK);
        }
    }

    @GetMapping("/findbyid/{productId}")
    @ResponseBody
    public ResponseEntity findProductById(@PathVariable String productId) {
        try {
            Product product = queryGateway.query(new FindProductByIdQuery(productId), ResponseTypes.instanceOf(Product.class))
                    .join();
            return new ResponseEntity(modelMapper.map(product, ProductDTO.class), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(String.format("Product with id: %s can not be found!!!", productId), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/eventStore/{productId}")
    @ResponseBody
    public Stream eventStore(@PathVariable String productId) {
        return eventStore.readEvents(productId).asStream();
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity updateProduct(@PathVariable String productId, @Validated @RequestBody ProductDTO request) {
        try {
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

            return new ResponseEntity(productUpdated, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(String.format("Product with id: %s can not be found!!!", productId), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable String productId) {
        try {
            Product product = queryGateway.query(new FindProductByIdQuery(productId),
                    ResponseTypes.instanceOf(Product.class)).join();
            if (product == null) {
                throw new RuntimeException("Product not found");
            }
//            ProductDeletedEvent productDeletedEvent = new ObjectMapper().convertValue(product, ProductDeletedEvent.class);
//            productInventoryEventHandler.delete(productDeletedEvent);

            commandGateway.send(new DeleteProductCommand(productId));

            return new ResponseEntity<>(String.format("Product with id: %s has been deleted successfully!", productId), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format("Product with id: %s can not be found!!!", productId), HttpStatus.NOT_FOUND);
        }
    }
}
