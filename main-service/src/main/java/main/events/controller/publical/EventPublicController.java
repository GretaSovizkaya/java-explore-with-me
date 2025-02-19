package main.events.controller.publical;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import main.events.dto.EventFullDto;
import main.events.dto.EventParamsDto;
import main.events.dto.EventShortDto;
import main.events.service.EventService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/events")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventPublicController {
    EventService eventService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping
    public List<EventShortDto> getAllEvents(
            @RequestParam(defaultValue = "") final String text,
            @RequestParam(required = false) final List<Long> categories,
            @RequestParam(required = false) final Boolean paid,
            @RequestParam(required = false) final String rangeStart,
            @RequestParam(required = false) final String rangeEnd,
            @RequestParam(defaultValue = "false") final boolean onlyAvailable,
            @RequestParam(required = false) final String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
            @RequestParam(defaultValue = "10") @Positive final int size,
            final HttpServletRequest request) {

        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart, formatter) : LocalDateTime.now();
        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd, formatter) : LocalDateTime.now().plusYears(20);

        EventParamsDto eventParams = new EventParamsDto();
        eventParams.setText(text);
        eventParams.setCategories(categories);
        eventParams.setPaid(paid);
        eventParams.setRangeStart(start);
        eventParams.setRangeEnd(end);
        eventParams.setOnlyAvailable(onlyAvailable);
        eventParams.setFrom(from);
        eventParams.setSize(size);
        if (Objects.nonNull(sort)) {
            eventParams.setSort(sort.toString());
        }

        log.info("Запрос Public на получения событий с фильтром");
        return eventService.getAllEventsPublic(eventParams, request);

    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable(value = "eventId") @Min(1) Long eventId,
                                     HttpServletRequest request) {
        log.info("GET запрос на получения полной информации о событии с  id= {}", eventId);
        return eventService.getEventsById(eventId, request);
    }

}