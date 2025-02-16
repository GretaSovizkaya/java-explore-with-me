package main.events.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import main.events.model.enums.EventUserState;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserDto extends UpdateEventBaseDto {
    EventUserState stateAction;
}
