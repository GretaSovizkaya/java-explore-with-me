package main.compilations.controller.publical;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import main.compilations.dto.CompilationResponseDto;
import main.compilations.service.CompilationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicCompilationController {

    CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationResponseDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Запрос на удаление подборки событий");
        return compilationService.getCompilation(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationResponseDto findCompilationById(@PathVariable Long compId) {
        log.info("Запрос на получение подборки с id =  {} ", +compId);
        return compilationService.findCompilationById(compId);
    }
}