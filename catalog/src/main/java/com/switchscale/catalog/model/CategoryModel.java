package com.switchscale.catalog.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "catalog")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryModel {
    
    @Id
    private String id;

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    @Size(max = 50, message = "Parent id cannot exceed 50 characters")
    private String parentId;

}
