package main.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import main.location.dto.LocationDto;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventBaseDto {
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
    int participantLimit;
    boolean requestModeration;
    String title;
}
