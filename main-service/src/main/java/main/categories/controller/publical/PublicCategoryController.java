package main.categories.controller.publical;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import main.categories.dto.CategoryDto;
import main.categories.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PublicCategoryController {
    CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Запрос на получение категорий списком с размерами");
        return categoryService.getCategory(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable(value = "catId") Long id) {
        log.info("Запрос на получение категории с id={}", id);
        return categoryService.getCategoryById(id);
    }
}