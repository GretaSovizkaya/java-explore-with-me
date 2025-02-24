package main.compilations.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import main.events.dto.EventShortDto;

import java.util.Set;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationResponseDto {
    Long id;
    Set<EventShortDto> events;
    Boolean pinned;
    String title;
}
