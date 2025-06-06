package com.cropMatch.service.extra;


import com.cropMatch.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> getActiveCategories();
    Optional<Category> getCategoryByName(String name);

}
