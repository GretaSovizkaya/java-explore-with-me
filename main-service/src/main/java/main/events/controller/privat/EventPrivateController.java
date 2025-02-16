package main.events.controller.privat;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.events.dto.*;
import main.events.service.EventService;
import main.requests.dto.ParticipationRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventPrivateController {

    EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllEventsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        List<EventShortDto> events = eventService.getEventsByUserId(userId, from, size);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EventFullDto> addEvent(
            @PathVariable Long userId,
            @RequestBody @Valid NewEventDto input) {
        EventFullDto event = eventService.createEvents(userId, input);
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getFullEventByOwner(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        EventFullDto event = eventService.getEventsByUserIdAndEventId(userId, eventId);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByOwner(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventUserDto inputUpdate) {
        EventFullDto updatedEvent = eventService.updateEventsByUserIdAndEventId(userId, eventId, inputUpdate);
        return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getAllRequestByEventFromOwner(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        List<ParticipationRequestDto> requests = eventService.getAllParticipationRequestsByOwner(userId, eventId);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<Map<String, List<ParticipationRequestDto>>> approveRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequestDto requestUpdateDto) {
        Map<String, List<ParticipationRequestDto>> result = eventService.approveRequests(userId, eventId, requestUpdateDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}