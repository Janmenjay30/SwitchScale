package com.switchscale.catalog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.switchscale.catalog.model.CategoryModel;
import com.switchscale.catalog.service.CatelogService;


@RestController
@RequestMapping("/categories")
public class CategoryController {

	private final CatelogService catelogService;

	public CategoryController(CatelogService catelogService) {
		this.catelogService = catelogService;
	}

	@PostMapping
	public ResponseEntity<CategoryModel> createCategory(@Valid @RequestBody CategoryModel category) {
		return ResponseEntity.status(HttpStatus.CREATED).body(catelogService.createCategory(category));
	}

	@GetMapping("/main")
	public ResponseEntity<List<CategoryModel>> getMainCategories() {
		return ResponseEntity.ok(catelogService.getMainCategories());
	}

	@GetMapping("/{parentId}/subcategories")
	public ResponseEntity<List<CategoryModel>> getSubCategories(@PathVariable String parentId) {
		return ResponseEntity.ok(catelogService.getSubCategories(parentId));
	}
}
