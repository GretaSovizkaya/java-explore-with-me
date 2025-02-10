package client;

import dto.StatDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import dto.StatResponseDto;


import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsClient {
    RestTemplate restTemplate;
    String statsServiceUrl = "http://localhost:8080/stats";

    public void sendHit(StatDto hitDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<StatDto> request = new HttpEntity<>(hitDto, headers);
        restTemplate.exchange(statsServiceUrl + "/hit", HttpMethod.POST, request, Void.class);
    }

    public List<StatResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String url = String.format("%s?start=%s&end=%s&unique=%b", statsServiceUrl, start, end, unique);
        if (uris != null && !uris.isEmpty()) {
            url += "&uris=" + String.join(",", uris);
        }
        ResponseEntity<StatResponseDto[]> response = restTemplate.getForEntity(url, StatResponseDto[].class);
        return List.of(response.getBody());
    }
}
