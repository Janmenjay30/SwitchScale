package com.switchscale.catalog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.switchscale.catalog.model.ProductModel;
import com.switchscale.catalog.service.CatelogService;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final CatelogService catelogService;

    public ProductController(CatelogService catelogService) {
        this.catelogService = catelogService;
    }

    @PostMapping
    public ResponseEntity<ProductModel> createProduct(@Valid @RequestBody ProductModel product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catelogService.createProduct(product));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductModel>> getProductsByCategory(@PathVariable String categoryId) {
        return ResponseEntity.ok(catelogService.getProductsByCategory(categoryId));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductModel> getProductById(@PathVariable String productId) {
        return ResponseEntity.ok(catelogService.getProductById(productId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductModel>> searchProductsByName(@RequestParam String name) {
        return ResponseEntity.ok(catelogService.searchProductsByName(name));
    }
}
