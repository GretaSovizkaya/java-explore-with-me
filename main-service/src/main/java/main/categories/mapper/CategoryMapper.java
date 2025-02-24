package main.categories.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import main.categories.dto.CategoryDto;
import main.categories.dto.CategoryRequestDto;
import main.categories.model.Category;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {
    public static CategoryDto toCategoryOutDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(CategoryRequestDto categoryRequestDto) {
        Category category = new Category(0,
                categoryRequestDto.getName());
        return category;
    }
}