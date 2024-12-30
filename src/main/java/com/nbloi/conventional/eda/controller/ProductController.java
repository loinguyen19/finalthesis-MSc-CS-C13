package com.nbloi.conventional.eda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbloi.conventional.eda.dto.ProductDTO;
import com.nbloi.conventional.eda.event.ProductCreatedEvent;
import com.nbloi.conventional.eda.event.ProductInventoryEvent;
import com.nbloi.conventional.eda.entity.Product;
import com.nbloi.conventional.eda.service.ProductInventoryEventHandler;
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


@RestController
@RequestMapping("/eda/api")
public class ProductController {

    @Autowired
    private final ModelMapper modelMapper;
    @Autowired
    private ProductInventoryEventHandler productInventoryEventHandler;


    public ProductController(ProductInventoryEventHandler productInventoryEventHandler, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.productInventoryEventHandler = productInventoryEventHandler;
    }

    @PostMapping("/create-product")
    public ResponseEntity<String> createProduct(@RequestBody ProductDTO request) {
        String productId = UUID.randomUUID().toString();
        String name = request.getName();
        BigDecimal price = request.getPrice();
        int stock = request.getStock();
        String currency = request.getCurrency();
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(productId, name, price, stock, currency);
        try {
            productInventoryEventHandler.on(productCreatedEvent);
            return new ResponseEntity<>(productId, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-listofproducts")
    public ResponseEntity createListOfProduct(@RequestBody ProductDTO []requestList) {
        try {
            List<ProductDTO> productsCreatedList = new ArrayList<>();
            for (ProductDTO request : requestList) {
                String productId = UUID.randomUUID().toString();
                String name = request.getName();
                BigDecimal price = request.getPrice();
                int stock = request.getStock();
                String currency = request.getCurrency();

                ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(productId, name, price, stock, currency);
                productInventoryEventHandler.on(productCreatedEvent);
                request.setProductId(productId);

                productsCreatedList.add(request);
            }
            return new ResponseEntity<>(productsCreatedList, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Your product requests can not be processed. Please review request payload",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/products")
    public List<ProductDTO> findAllProducts() {
        List<Product> listProduct = productInventoryEventHandler.readAllProducts();

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
        Product product = productInventoryEventHandler.readProductById(productId);
        return modelMapper.map(product, ProductDTO.class);
    }

    @PutMapping("/products/update/{productId}")
    public String updateProduct(@PathVariable String productId, @Validated @RequestBody ProductDTO request) {
        Product product = productInventoryEventHandler.readProductById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        ProductInventoryEvent productInventoryEvent = new ObjectMapper().convertValue(request, ProductInventoryEvent.class);

        productInventoryEventHandler.on(productInventoryEvent);
        Product productUpdated = productInventoryEventHandler.readProductById(productId);

        return "Product with id: " + productId + " and " + " name: " + productUpdated.getName() + " was successfully updated";
    }

    @DeleteMapping("/products/delete/{productId}")
    public String deleteProduct(@PathVariable String productId) {
        Product product = productInventoryEventHandler.readProductById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        ProductInventoryEvent productInventoryEvent = new ObjectMapper().convertValue(product, ProductInventoryEvent.class);
        productInventoryEventHandler.off(productInventoryEvent);
        return "Product with id: " + productId + " and name: " + product.getName() + " was successfully deleted";
    }
}
