
package server.repository;

import dto.StatResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import server.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository

public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("""
            SELECT new dto.StatResponseDto(s.ip, s.uri, COUNT(DISTINCT s.ip))
            FROM Stats AS s
            WHERE s.timestamp BETWEEN :start AND :end AND s.uri IN :uris
            GROUP BY s.ip, s.uri
            ORDER BY COUNT(DISTINCT s.ip) DESC
            """)
    List<StatResponseDto> findAllWithUniqueIpWithUris(List<String> uris,
                                                      LocalDateTime start,
                                                      LocalDateTime end);

    @Query("""
            SELECT new dto.StatResponseDto(s.ip, s.uri, COUNT(DISTINCT s.ip))
            FROM Stats AS s
            WHERE s.timestamp BETWEEN :start AND :end
            GROUP BY s.ip, s.uri
            ORDER BY COUNT(DISTINCT s.ip) DESC
            """)
    List<StatResponseDto> findAllWithUniqueIpWithoutUris(LocalDateTime start,
                                                         LocalDateTime end);

    @Query("""
            SELECT new dto.StatResponseDto(s.ip, s.uri, COUNT(s.ip))
            FROM Stats AS s
            WHERE s.timestamp BETWEEN :start AND :end AND s.uri IN :uris
            GROUP BY s.ip, s.uri
            ORDER BY COUNT(s.ip) DESC
            """)
    List<StatResponseDto> findAllWithUris(List<String> uris,
                                          LocalDateTime start,
                                          LocalDateTime end);

    @Query("""
            SELECT new dto.StatResponseDto(s.ip, s.uri, COUNT(s.ip))
            FROM Stats AS s
            WHERE s.timestamp BETWEEN :start AND :end
            GROUP BY s.ip, s.uri
            ORDER BY COUNT(s.ip) DESC
            """)
    List<StatResponseDto> findAllWithoutUris(LocalDateTime start,
                                             LocalDateTime end);
}
