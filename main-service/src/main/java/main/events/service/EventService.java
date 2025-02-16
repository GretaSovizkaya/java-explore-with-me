package main.events.service;

import jakarta.servlet.http.HttpServletRequest;
import main.events.dto.*;
import main.requests.dto.ParticipationRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface EventService {
    List<EventFullDto> getAllEventsAdmin(EventAdminParamsDto eventAdminParams);

    EventFullDto updateEventsAdmin(Long eventId, UpdateEventAdminDto updateEvent);

    List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto createEvents(Long userId, NewEventDto newEventDto);

    EventFullDto getEventsByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto updateEventsByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserDto eventUpdate);

    List<ParticipationRequestDto> getAllParticipationRequestsByOwner(Long userId, Long eventId);

    List<EventShortDto> getAllEventsPublic(EventParamsDto eventParams, HttpServletRequest request);

    EventFullDto getEventsById(Long eventId, HttpServletRequest request);

    Map<String, List<ParticipationRequestDto>> approveRequests(final Long userId, final Long eventId,
                                                               final EventRequestStatusUpdateRequestDto requestUpdateDto);
}
