
package server.contoller;

import dto.StatDto;
import dto.StatResponseDto;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import server.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/stats")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsController {
    StatsService statsService;

    @GetMapping("/stats")
    public ResponseEntity<List<StatResponseDto>> getStats(@RequestParam LocalDateTime start,
                                                          @RequestParam LocalDateTime end,
                                                          @RequestParam(required = false) List<String> uris,
                                                          @RequestParam(defaultValue = "false") boolean unique) {
        List<StatResponseDto> stats = statsService.getStats(start, end, uris, unique);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveEndpointHit(@Valid @RequestBody StatDto endpointHitDto) {
        log.info("Saving endpoint hit: {}", endpointHitDto);
        statsService.saveHit(endpointHitDto);
    }
}
