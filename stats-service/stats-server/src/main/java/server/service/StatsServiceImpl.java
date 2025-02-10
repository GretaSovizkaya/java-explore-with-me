package server.service;

import dto.StatDto;
import dto.StatResponseDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.model.Stats;
import server.model.mapper.StatMapper;
import server.repository.StatsRepository;
import server.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsServiceImpl implements StatsService {
    StatsRepository statsRepository;
    StatMapper statMapper;

    @Override
    @Transactional
    public StatDto saveHit(StatDto endpointHitDto) {
        Stats stats = statMapper.toStat(endpointHitDto);

        return statMapper.toStatDto(statsRepository.save(stats));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала start: " + start + "и дата окончания end: " + end
                    + "не могут быть равны или противоречить друг другу");
        }
        if (unique) {
            if (uris != null) {
                return statsRepository.findAllWithUniqueIpWithUris(uris, start, end);
            }
            return statsRepository.findAllWithUniqueIpWithoutUris(start, end);
        } else {
            if (uris != null) {
                return statsRepository.findAllWithUris(uris, start, end);
            }
            return statsRepository.findAllWithoutUris(start, end);
        }
    }
}
