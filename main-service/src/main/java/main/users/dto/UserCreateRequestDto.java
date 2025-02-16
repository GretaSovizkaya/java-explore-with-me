package main.users.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequestDto {
    @NotBlank
    @Size(min = 2, max = 250)
    String name;
    @Email
    @NotBlank
    @Size(min = 6, max = 254)
    String email;
}