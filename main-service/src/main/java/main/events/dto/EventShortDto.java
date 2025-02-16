package main.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import main.categories.dto.CategoryDto;
import main.users.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    Long id;
    String annotation;
    CategoryDto category;
    Integer confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    UserShortDto initiator;
    boolean paid;
    String title;
    Long views;
}
