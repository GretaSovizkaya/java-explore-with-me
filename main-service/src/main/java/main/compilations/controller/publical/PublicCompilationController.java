package main.compilations.controller.publical;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.compilations.dto.CompilationResponseDto;
import main.compilations.service.CompilationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PublicCompilationController {

    CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationResponseDto>> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                                        @RequestParam(defaultValue = "0") Integer from,
                                                                        @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(compilationService.getCompilation(pinned, from, size));
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationResponseDto> getCompilationById(@PathVariable Long compId) {
        return ResponseEntity.ok(compilationService.findCompilationById(compId));
    }
}