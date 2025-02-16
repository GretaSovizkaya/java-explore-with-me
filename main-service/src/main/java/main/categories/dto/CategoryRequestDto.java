package main.categories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequestDto {
    @NotBlank
    @Size(max = 50, message =
            "max length is 50 symbols")
    String name;
}
