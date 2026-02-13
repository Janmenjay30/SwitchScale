package com.switchscale.inventory.service;

import org.springframework.stereotype.Service;

import com.switchscale.inventory.model.Product;
import com.switchscale.inventory.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final ProductRepository productRepository;

    public Product updateStock(Long id,int qty){
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStockQuantity(product.getStockQuantity() + qty);
        return productRepository.save(product);
    }
}
