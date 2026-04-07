package com.switchscale.catalog.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.switchscale.catalog.model.ProductModel;

public interface ProductRepository extends MongoRepository<ProductModel, String> {
    
    
    List<ProductModel> findByCategoryId(String categoryId);

    List<ProductModel> findByNameContainingIgnoreCase(String name);

}
