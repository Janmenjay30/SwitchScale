package com.switchscale.cart.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.switchscale.cart.dto.ProductDTO;

@FeignClient(name = "catalog-service", url = "http://localhost:8002")
public interface CatalogClient {
    

    @GetMapping("/products/{productId}")
    
    ProductDTO getProductById(@PathVariable("productId") String productId);
}
