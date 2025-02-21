package main.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import main.exceptions.validate.TimeAtLeastTwoHours;
import main.location.dto.LocationDto;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventBaseDto {
    @Length(min = 20, max = 2000)
    String annotation;

    Long category;

    @Length(min = 20, max = 7000)
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @TimeAtLeastTwoHours
    LocalDateTime eventDate;

    @Valid
    LocationDto location;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration;

    @Size(min = 3, max = 120)
    String title;

    Boolean paid;

}