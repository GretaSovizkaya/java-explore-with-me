package main.requests.service;

import main.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestByUserId(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
