package main.categories.service;

import main.categories.dto.CategoryDto;
import main.categories.dto.CategoryRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    CategoryDto getCategoryById(Long id);
    List<CategoryDto> getCategory(Integer from, Integer size);
    CategoryDto createCategory(CategoryRequestDto categoryRequestDto);
    void deleteCategory(Long id);
    CategoryDto updateCategory(Long id, CategoryRequestDto categoryRequestDto);
}
