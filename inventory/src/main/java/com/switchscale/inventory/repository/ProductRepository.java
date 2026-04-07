package com.switchscale.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.switchscale.inventory.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
