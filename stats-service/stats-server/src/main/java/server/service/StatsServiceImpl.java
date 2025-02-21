package server.service;

import dto.StatDto;
import dto.StatInDto;
import dto.StatResponseDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.model.Stats;
import server.model.mapper.StatMapper;
import server.repository.StatsRepository;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsServiceImpl implements StatsService {
    StatsRepository statsRepository;

    @Override
    @Transactional
    public StatDto saveHit(StatInDto statInDto) {
        Stats stats = StatMapper.toStat(statInDto);

        return StatMapper.toStatDto(statsRepository.save(stats));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new InvalidParameterException("Дата начала start: " + start + "и дата окончания end: " + end + "не могут быть равны или противоречить друг другу");
        }

        if (unique) {
            if (uris == null || uris.isEmpty()) {
                return statsRepository.findAllWithUniqueIpWithoutUris(start, end);
            }
            return statsRepository.findAllWithUniqueIpWithUris(uris, start, end);
        } else {
            if (uris == null || uris.isEmpty()) {
                return statsRepository.findAllWithoutUris(start, end);
            }
            return statsRepository.findAllWithUris(uris, start, end);
        }
    }
}
