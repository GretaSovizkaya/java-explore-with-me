package main.events.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventParamsDto {
    String text;
    List<Long> categories;
    Boolean paid;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;

    @Builder.Default
    Boolean onlyAvailable = false;

    @Builder.Default
    Integer from = 0;

    @Builder.Default
    Integer size = 10;

    String sort;
}
