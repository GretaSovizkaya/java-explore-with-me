package main.events.controller.privat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import main.events.dto.*;
import main.events.service.EventService;
import main.requests.dto.ParticipationRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users/{userId}/events")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventPrivateController {
    EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllEventsByUserId(@PathVariable(value = "userId") @Min(1) Long userId,
                                                    @RequestParam(value = "from", defaultValue = "0")
                                                    @PositiveOrZero Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10")
                                                    @Positive Integer size) {
        log.info("Запрос на получения событий пользователя с id= {}", userId);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable(value = "userId") @Min(1) Long userId,
                                 @RequestBody @Valid NewEventDto input) {
        log.info("Cоздание события от пользователя с id= {}", userId);
        EventFullDto eventFullDto = eventService.createEvents(userId, input);
        log.info("Cобытия от пользователя с id= {} создано", userId);
        return eventFullDto;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getFullEventByOwner(@PathVariable(value = "userId") @Min(1) Long userId,
                                            @PathVariable(value = "eventId") @Min(1) Long eventId) {
        log.info(" Запрос на получения полной информации о событии для пользователя с id= {}", userId);
        return eventService.getEventsByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByOwner(@PathVariable(value = "userId") @Min(0) Long userId,
                                           @PathVariable(value = "eventId") @Min(0) Long eventId,
                                           @RequestBody @Valid UpdateEventUserRequestDto inputUpdate) {
        log.info(" Запрос на обновление события от пользователя с id= {}", userId);
        return eventService.updateEventsByUserIdAndEventId(userId, eventId, inputUpdate);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getAllRequestByEventFromOwner(@PathVariable(value = "userId") @Min(1) Long userId,
                                                                       @PathVariable(value = "eventId") @Min(1) Long eventId) {
        log.info("Запрос на получение информации о всех запросах об участии в событии для пользователя с id= {}", userId);
        return eventService.getAllParticipationRequestsByOwner(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public Map<String, List<ParticipationRequestDto>> approveRequests(@PathVariable final Long userId,
                                                                      @PathVariable final Long eventId,
                                                                      @RequestBody @Valid final EventRequestStatusUpdateRequestDto requestUpdateDto) {
        return eventService.approveRequests(userId, eventId, requestUpdateDto);
    }
}