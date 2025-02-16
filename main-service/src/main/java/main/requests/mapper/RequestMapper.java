package main.requests.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import main.requests.dto.ParticipationRequestDto;
import main.requests.model.Request;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .created(request.getCreated())
                .requester(request.getId())
                .status(request.getStatus())
                .build();
    }

    public static Request toRequest(ParticipationRequestDto participationRequestDto) {
        return Request.builder()
                .id(participationRequestDto.getId())
                .event(null)
                .created(participationRequestDto.getCreated())
                .requestor(null)
                .status(participationRequestDto.getStatus())
                .build();
    }
}