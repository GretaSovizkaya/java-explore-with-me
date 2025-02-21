
package server.contoller;

import dto.StatInDto;
import dto.StatResponseDto;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import server.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsController {
    StatsService statsService;

    @GetMapping("/stats")
    public List<StatResponseDto> getStats(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                          @RequestParam(defaultValue = "", required = false) List<String> uris,
                                          @RequestParam(defaultValue = "false", required = false) boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveEndpointHit(@RequestBody @Valid StatInDto statInDto) {
        log.info("Saving endpoint hit: {}", statInDto);
        statsService.saveHit(statInDto);
    }
}
