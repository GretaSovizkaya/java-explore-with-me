package main.requests.controller;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.requests.dto.ParticipationRequestDto;
import main.requests.service.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestPrivateController {

    RequestService requestService;

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> addRequest(
            @PathVariable @Min(0) Long userId,
            @RequestParam @Min(0) Long eventId) {
        ParticipationRequestDto request = requestService.createRequest(userId, eventId);
        return new ResponseEntity<>(request, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getAllRequests(
            @PathVariable @Min(0) Long userId) {
        List<ParticipationRequestDto> requests = requestService.getRequestByUserId(userId);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(
            @PathVariable @Min(0) Long userId,
            @PathVariable @Min(0) Long requestId) {
        ParticipationRequestDto canceledRequest = requestService.cancelRequest(userId, requestId);
        return new ResponseEntity<>(canceledRequest, HttpStatus.OK);
    }
}