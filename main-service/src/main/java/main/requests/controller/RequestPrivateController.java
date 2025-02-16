package main.requests.controller;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.requests.dto.ParticipationRequestDto;
import main.requests.service.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class RequestPrivateController {
    RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable(value = "userId") @Min(0) Long userId,
                                              @RequestParam(name = "eventId") @Min(0) Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAllRequests(@PathVariable(value = "userId") @Min(0) Long userId) {
        return requestService.getRequestByUserId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto canceledRequest(@PathVariable(value = "userId") @Min(0) Long userId,
                                                   @PathVariable(value = "requestId") @Min(0) Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}