package com.switchscale.catalog.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.switchscale.catalog.exception.ResourceNotFoundException;
import com.switchscale.catalog.model.CategoryModel;
import com.switchscale.catalog.model.ProductModel;
import com.switchscale.catalog.repository.CategoryRepository;
import com.switchscale.catalog.repository.ProductRepository;

@Service
public class CatelogService {
    
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CatelogService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public CategoryModel createCategory(CategoryModel category){
        return categoryRepository.save(category);
    }

    public List<CategoryModel> getMainCategories(){
        return categoryRepository.findByParentIdIsNull();
    }

    public List<CategoryModel> getSubCategories(String parentId){
        return categoryRepository.findByParentId(parentId);
    }

    // Product Methods

    public ProductModel createProduct(ProductModel product){
        validateProductPayload(product);
        return productRepository.save(product);
    }

    public ProductModel updateProduct(String productId, ProductModel payload) {
        ProductModel existing = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for id: " + productId));

        validateProductPayload(payload);

        existing.setCategoryId(payload.getCategoryId());
        existing.setName(payload.getName());
        existing.setDescription(payload.getDescription());
        existing.setPrice(payload.getPrice());
        existing.setMrp(payload.getMrp());
        existing.setWeight(payload.getWeight());
        existing.setImageUrl(payload.getImageUrl());

        return productRepository.save(existing);
    }

    public void deleteProduct(String productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found for id: " + productId);
        }
        productRepository.deleteById(productId);
    }

    private void validateProductPayload(ProductModel product) {
        if (!categoryRepository.existsById(product.getCategoryId())) {
            throw new ResourceNotFoundException("Category not found for id: " + product.getCategoryId());
        }

        if (product.getMrp() < product.getPrice()) {
            throw new IllegalArgumentException("MRP cannot be less than price");
        }
    }

    public List<ProductModel> getProductsByCategory(String categoryId){
        return productRepository.findByCategoryId(categoryId);
    }

    public ProductModel getProductById(String productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for id: " + productId));
    }

    public List<ProductModel> searchProductsByName(String name){
        return productRepository.findByNameContainingIgnoreCase(name);
    }


}
