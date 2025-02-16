package main.categories.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import main.categories.dto.CategoryDto;
import main.categories.dto.CategoryRequestDto;
import main.categories.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Validated
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryRequestDto));
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long catId,
                                                      @Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        return ResponseEntity.ok(categoryService.updateCategory(catId, categoryRequestDto));
    }
}