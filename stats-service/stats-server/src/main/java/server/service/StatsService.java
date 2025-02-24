
package server.service;

import dto.StatDto;
import dto.StatInDto;
import dto.StatResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    StatDto saveHit(StatInDto statInDto);

    List<StatResponseDto> getStats(LocalDateTime start,
                                   LocalDateTime end,
                                   List<String> uris,
                                   boolean unique);
}
