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
public class EventAdminParamsDto {
    List<Long> users;
    List<String> states;
    List<Long> categories;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    @Builder.Default
    Integer from = 0;
    @Builder.Default
    Integer size = 10;
}
