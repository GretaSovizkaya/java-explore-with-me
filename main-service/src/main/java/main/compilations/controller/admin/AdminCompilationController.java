package main.compilations.controller.admin;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import main.compilations.dto.CompilationRequestDto;
import main.compilations.dto.CompilationResponseDto;
import main.compilations.dto.NewCompilationDto;
import main.compilations.service.CompilationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AdminCompilationController {
    CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto createCompilation(@RequestBody @Valid CompilationRequestDto compilationDto) {
        log.info("Запрос на создание подборки событий");
        return compilationService.createCompilation(compilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationResponseDto updateCompilation(@RequestBody @Valid NewCompilationDto update,
                                                    @PathVariable Long compId) {
        log.info("Запрос на обнавление подборки событий");
        return compilationService.updateCompilation(compId, update);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Запрос на удаление подборки событий");
        compilationService.deleteCompilation(compId);
    }

}