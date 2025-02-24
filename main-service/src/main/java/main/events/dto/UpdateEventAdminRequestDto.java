package main.events.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import main.events.model.enums.EventAdminState;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequestDto extends UpdateEventBaseDto {
    EventAdminState stateAction;
}
