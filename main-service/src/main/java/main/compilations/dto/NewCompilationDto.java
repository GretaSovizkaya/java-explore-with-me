package main.compilations.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto { //CompilationUpdateRequestDto
    Long id;
    Set<Long> events;
    Boolean pinned;
    @Size(max = 50)
    String title;
}
