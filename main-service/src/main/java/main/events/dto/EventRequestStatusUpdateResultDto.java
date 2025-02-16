package main.events.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import main.requests.dto.ParticipationRequestDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateResultDto {
    List<ParticipationRequestDto> confirmedRequests;
    List<ParticipationRequestDto> rejectedRequests;

}