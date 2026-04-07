package com.switchscale.inventory.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.switchscale.inventory.service.InventoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.switchscale.inventory.model.Product;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    
    @PostMapping("/update/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id,@RequestBody int qty){
        return ResponseEntity.ok(inventoryService.updateStock(id,qty));
    }
    

}
