package com.switchscale.catalog.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

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
        existing.setCategoryIds(payload.getCategoryIds());
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
        List<String> resolvedCategoryIds = resolveCategoryIds(product);

        if (resolvedCategoryIds.isEmpty()) {
            throw new IllegalArgumentException("At least one category is required");
        }

        if (resolvedCategoryIds.size() > 3) {
            throw new IllegalArgumentException("A product can belong to at most 3 categories");
        }

        for (String categoryId : resolvedCategoryIds) {
            if (!categoryRepository.existsById(categoryId)) {
                throw new ResourceNotFoundException("Category not found for id: " + categoryId);
            }
        }

        if (product.getPrice() == null || product.getMrp() == null) {
            throw new IllegalArgumentException("Price and MRP are required");
        }

        if (product.getMrp() < product.getPrice()) {
            throw new IllegalArgumentException("MRP cannot be less than price");
        }

        product.setCategoryIds(resolvedCategoryIds);
        product.setCategoryId(resolvedCategoryIds.get(0));
    }

    public List<ProductModel> getProductsByCategory(String categoryId){
        LinkedHashMap<String, ProductModel> merged = new LinkedHashMap<>();

        for (ProductModel product : productRepository.findByCategoryIdsContaining(categoryId)) {
            merged.put(product.getId(), product);
        }

        // Backward compatibility for legacy rows saved with only categoryId.
        for (ProductModel product : productRepository.findByCategoryId(categoryId)) {
            merged.putIfAbsent(product.getId(), product);
        }

        return new ArrayList<>(merged.values());
    }

    public ProductModel getProductById(String productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for id: " + productId));
    }

    public List<ProductModel> searchProductsByName(String name){
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    private List<String> resolveCategoryIds(ProductModel product) {
        LinkedHashSet<String> merged = new LinkedHashSet<>();

        if (product.getCategoryIds() != null) {
            product.getCategoryIds().stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .forEach(merged::add);
        }

        if (product.getCategoryId() != null && !product.getCategoryId().isBlank()) {
            merged.add(product.getCategoryId().trim());
        }

        return new ArrayList<>(merged);
    }


}
