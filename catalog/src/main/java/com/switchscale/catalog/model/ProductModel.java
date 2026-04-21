package com.switchscale.catalog.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Document(collection = "products")
public class ProductModel {
    @Id
    private String id;

    // Primary category retained for compatibility with existing frontend payloads.
    private String categoryId;

    // A product can belong to up to 3 categories.
    @Size(max = 3, message = "A product can belong to at most 3 categories")
    private List<String> categoryIds = new ArrayList<>();
    
    @NotBlank(message = "Product name is required")
    @Size(max = 120, message = "Product name cannot exceed 120 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Double price;

    @NotNull(message = "MRP is required")
    @Positive(message = "MRP must be greater than zero")
    private Double mrp;

    @NotBlank(message = "Weight is required")
    private String weight;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;
    
    private boolean isActive = true;

    public ProductModel() {
    }

    public ProductModel(String id, String categoryId, List<String> categoryIds, String name, String description,
            Double price, Double mrp, String weight, String imageUrl, boolean isActive) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryIds = categoryIds;
        this.name = name;
        this.description = description;
        this.price = price;
        this.mrp = mrp;
        this.weight = weight;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<String> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }

    public Double getMrp() {
        return mrp;
    }

    public void setMrp(Double mrp) {
        this.mrp = mrp;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    
}