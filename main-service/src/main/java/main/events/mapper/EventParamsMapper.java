package main.events.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.events.dto.EventParamsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventParamsMapper {

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EventParamsDto toEventParamsDto(
            String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            boolean onlyAvailable,
            String sort,
            int from,
            int size) {

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
        if (sort != null) {
            eventParams.setSort(sort);
        }

        return eventParams;
    }
}