package main.compilations.controller.admin;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.compilations.dto.CompilationRequestDto;
import main.compilations.dto.CompilationResponseDto;
import main.compilations.dto.NewCompilationDto;
import main.compilations.service.CompilationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCompilationController {

    CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationResponseDto> createCompilation(
            @RequestBody @Valid CompilationRequestDto compilationDto) {
        CompilationResponseDto createdCompilation = compilationService.createCompilation(compilationDto);
        return new ResponseEntity<>(createdCompilation, HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationResponseDto> updateCompilation(
            @PathVariable Long compId,
            @RequestBody @Valid NewCompilationDto update) {
        CompilationResponseDto updatedCompilation = compilationService.updateCompilation(compId, update);
        return new ResponseEntity<>(updatedCompilation, HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}