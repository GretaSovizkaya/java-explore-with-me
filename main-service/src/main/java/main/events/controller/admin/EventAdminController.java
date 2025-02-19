package main.events.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import main.events.dto.EventAdminParamsDto;
import main.events.dto.EventFullDto;
import main.events.dto.UpdateEventAdminRequestDto;
import main.events.service.EventService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class EventAdminController {
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
        log.info("запрос на получение всех событий (ADMIN)");
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
    public EventFullDto updateEventAdmin(@PathVariable(value = "eventId") @Min(1) Long eventId,
                                         @RequestBody @Valid UpdateEventAdminRequestDto inputUpdate) {

        log.info("Запрос на обновление списка событий");
        return eventService.updateEventsAdmin(eventId, inputUpdate);
    }
}