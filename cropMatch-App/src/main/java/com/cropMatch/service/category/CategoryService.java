package com.cropMatch.service.category;


import com.cropMatch.model.admin.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> getActiveCategories();
    Optional<Category> getCategoryByName(String name);

}
