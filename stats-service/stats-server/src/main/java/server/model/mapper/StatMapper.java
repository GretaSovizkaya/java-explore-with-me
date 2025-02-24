
package server.model.mapper;

import dto.StatDto;
import dto.StatInDto;
import server.model.Stats;


public class StatMapper {
    public static StatDto toStatDto(Stats stats) {
        return StatDto.builder()
                .app(stats.getApp())
                .uri(stats.getUri())
                .timestamp(stats.getTimestamp())
                .build();
    }

    public static Stats toStat(StatInDto statInDto) {
        return new Stats(
                null,
                statInDto.getApp(),
                statInDto.getUri(),
                statInDto.getIp(),
                statInDto.getTimestamp()
        );
    }
}
