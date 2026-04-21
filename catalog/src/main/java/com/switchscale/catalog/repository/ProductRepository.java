package com.switchscale.catalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.switchscale.catalog.model.ProductModel;

public interface ProductRepository extends MongoRepository<ProductModel, String> {

    List<ProductModel> findByCategoryId(String categoryId);

    List<ProductModel> findByCategoryIdsContaining(String categoryId);

    List<ProductModel> findByNameContainingIgnoreCase(String name);

    Optional<ProductModel> findByNameIgnoreCase(String name);

}
