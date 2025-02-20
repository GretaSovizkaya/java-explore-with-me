package main.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import main.location.dto.LocationDto;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank
    @Length(max = 2000, min = 20)
    String annotation;
    @NotNull
    @Positive
    Long category;
    @NotBlank
    @Length(max = 7000, min = 20)
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @NotNull
    @Valid
    LocationDto location;
    boolean paid;
    @PositiveOrZero
    int participantLimit;
    @Builder.Default
    boolean requestModeration = true;
    @NotBlank(message = "Название события не может быть пустым")
    @Size(min = 3, max = 120, message = "Название события должно содержать от 3 до 120 символов")
    String title;
}
