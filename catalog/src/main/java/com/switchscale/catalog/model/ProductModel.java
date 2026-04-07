package com.switchscale.catalog.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "products")
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
    @Id
    private String id;
    
    @NotBlank(message = "Category id is required")
    private String categoryId; // links to CategoryModel
    
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

    public String getCategoryId() {
        return categoryId;
    }

    public Double getPrice() {
        return price;
    }

    public Double getMrp() {
        return mrp;
    }
    
}