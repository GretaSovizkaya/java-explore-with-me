package main.events.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import main.requests.model.RequestStatus;

import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequestDto {
    @NotNull
    Set<Long> requestIds;
    @NotNull
    RequestStatus status;
}