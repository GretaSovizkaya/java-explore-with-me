package main.categories.controller.publical;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.categories.dto.CategoryDto;
import main.categories.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PublicCategoryController {

    CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(@RequestParam(required = false, defaultValue = "0") Integer from,
                                                           @RequestParam(required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.ok(categoryService.getCategory(from, size));
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long catId) {
        CategoryDto category = categoryService.getCategoryById(catId);
        return ResponseEntity.ok(category);
    }
}