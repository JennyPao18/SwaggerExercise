package org.adaschool.api.controller.product;

import org.adaschool.api.exception.ProductNotFoundException;
import org.adaschool.api.repository.product.Product;
import org.adaschool.api.service.product.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/products/")
public class ProductsController {

    private final ProductsService productsService;

    public ProductsController(@Autowired ProductsService productsService) {
        this.productsService = productsService;
    }

    // POST - createProduct
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productsService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    // GET - getAllProducts
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productsService.all();
        return ResponseEntity.ok(products);
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> findById(@PathVariable("id") String id) {
        Optional<Product> product = productsService.findById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            String errorMessage = "404 NOT_FOUND \"product with ID: " + id + " not found\"";
            throw new ProductNotFoundException(errorMessage);
        }
    }

    // PUT - updateProduct
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @RequestBody Product productDetails) {
        Optional<Product> existingProductOpt = productsService.findById(id);

        if (!existingProductOpt.isPresent()) {
            // Lanzar la excepción ProductNotFoundException con el mensaje esperado por el test
            throw new ProductNotFoundException("404 NOT_FOUND \"product with ID: " + id + " not found\"");
        }

        Product existingProduct = existingProductOpt.get();
        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setCategory(productDetails.getCategory());
        existingProduct.setPrice(productDetails.getPrice());

        Product updatedProduct = productsService.save(existingProduct);
        return ResponseEntity.ok(updatedProduct);
    }


    // DELETE - deleteProduct
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        Product existingProduct = productsService.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("404 NOT_FOUND \"product with ID: " + id + " not found\""));

        productsService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
