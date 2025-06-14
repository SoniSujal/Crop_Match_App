package com.cropMatch.controller.category;

import com.cropMatch.model.admin.Category;
import com.cropMatch.repository.category.CategoryRepository;
import com.cropMatch.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    private final CategoryRepository categoryRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<String> createCategory(@RequestBody Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Category name is required");
        }

        if (categoryRepository.findByName(category.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Category already exists");
        }

        category.setIsActive(true);
        category.setCreatedOn(LocalDateTime.now());
        categoryRepository.save(category);

        return ResponseEntity.ok("Category created successfully");
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Category>> getActiveCategories() {
        return ResponseEntity.ok(categoryService.getActiveCategories());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<String> toggleCategoryStatus(@PathVariable Integer id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setIsActive(!category.getIsActive());
            categoryRepository.save(category);
            return ResponseEntity.ok("Category status updated successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/inactive")
    public ResponseEntity<List<Category>> getInactiveCategories() {
        List<Category> inactiveCategories = categoryRepository.findByIsActiveFalse();
        return ResponseEntity.ok(inactiveCategories);
    }
}
