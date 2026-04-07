package com.switchscale.catalog.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.switchscale.catalog.model.CategoryModel;

public interface CategoryRepository extends MongoRepository<CategoryModel, String> {

    // fetches top leve; categories
    List<CategoryModel> findByParentIdIsNull();

    // fetched sub-categories of a category
    List<CategoryModel> findByParentId(String parentId);

}
