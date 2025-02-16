package main.categories.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.categories.dto.CategoryDto;
import main.categories.dto.CategoryRequestDto;
import main.categories.mapper.CategoryMapper;
import main.categories.model.Category;
import main.categories.repository.CategoryRepository;
import main.categories.service.CategoryService;
import main.exceptions.NotFoundException;
import main.exceptions.ValidatetionConflict;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;

    @Override
    public CategoryDto getCategoryById(Long id) {
        CategoryDto categoryResponseDto;
        categoryResponseDto = CategoryMapper.toCategoryOutDto(findCategory(id));
        return categoryResponseDto;
    }

    @Override
    public List<CategoryDto> getCategory(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return categoryRepository.findAll(pageRequest)
                .stream()
                .map(CategoryMapper::toCategoryOutDto)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryRequestDto categoryRequestDto) {
        return CategoryMapper.toCategoryOutDto(categoryRepository.save(CategoryMapper.toCategory(categoryRequestDto)));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Категория с id= " + id + " не найден");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryRequestDto categoryRequestDto) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Категория с id= " + id + " не найдена");
        }
        Category category = findCategory(id);

        if (categoryRequestDto.getName() != null && !category.getName().equals(categoryRequestDto.getName()) && checkName(categoryRequestDto.getName())) {

            category.setName(categoryRequestDto.getName());
            category = categoryRepository.save(category);

        }
        return CategoryMapper.toCategoryOutDto(category);
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Категории с id = " + id + " не существует"));
    }

    private boolean checkName(String name) {

        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ValidatetionConflict("Категория с названием " + name + " уже существует");
        }
        return true;
    }
    }
