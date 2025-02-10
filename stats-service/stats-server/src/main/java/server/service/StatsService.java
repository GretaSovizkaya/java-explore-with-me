
package server.service;

import dto.StatDto;
import dto.StatResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    StatDto saveHit(StatDto endpointHitDto);

    List<StatResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
