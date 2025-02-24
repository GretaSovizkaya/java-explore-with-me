package main.categories.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import main.categories.dto.CategoryDto;
import main.categories.dto.CategoryRequestDto;
import main.categories.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCategoryController {
    CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        log.info("Cоздание новой категории: {}", categoryRequestDto);
        return categoryService.createCategory(categoryRequestDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(value = "catId") Long id) {
        log.info("Удалении категории с id =  {} ", id);
        categoryService.deleteCategory(id);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable(value = "catId") @Min(1) Long id,
                                      @RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        log.info("Обновдение категории с id = {}", id);
        return categoryService.updateCategory(id, categoryRequestDto);
    }

}