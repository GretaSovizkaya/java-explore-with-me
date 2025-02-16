package main.events.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.events.dto.EventAdminParamsDto;
import main.events.dto.EventFullDto;
import main.events.dto.UpdateEventAdminDto;
import main.events.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminEventController {

    EventService eventService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping
    public List<EventFullDto> getEventsAdmin(
            @RequestParam(required = false) final List<Long> users,
            @RequestParam(required = false) final List<String> states,
            @RequestParam(required = false) final List<Long> categories,
            @RequestParam(required = false) final String rangeStart,
            @RequestParam(required = false) final String rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
            @RequestParam(defaultValue = "10") @Positive final int size) {
        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart, formatter) : LocalDateTime.now();
        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd, formatter) : LocalDateTime.now().plusYears(20);
        EventAdminParamsDto eventAdminParams =  new EventAdminParamsDto();
        eventAdminParams.setUsers(users);
        eventAdminParams.setStates(states);
        eventAdminParams.setCategories(categories);
        eventAdminParams.setRangeStart(start);
        eventAdminParams.setRangeEnd(end);
        eventAdminParams.setFrom(from);
        eventAdminParams.setSize(size);
        return eventService.getAllEventsAdmin(eventAdminParams);
    }


    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEventsAdmin(@PathVariable Long eventId,
                                                          @Valid @RequestBody UpdateEventAdminDto updateEvent) {
        return ResponseEntity.ok(eventService.updateEventsAdmin(eventId, updateEvent));
    }
}
