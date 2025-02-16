package main.requests.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.events.model.Event;
import main.events.model.enums.EventStatus;
import main.events.repository.EventRepository;
import main.exceptions.NotFoundException;
import main.exceptions.ValidatetionConflict;
import main.requests.dto.ParticipationRequestDto;
import main.requests.mapper.RequestMapper;
import main.requests.model.Request;
import main.requests.model.RequestStatus;
import main.requests.repository.RequestRepository;
import main.users.model.User;
import main.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestServiceImpl implements RequestService {
    RequestRepository requestRepository;
    UserRepository userRepository;
    EventRepository eventRepository;
    RequestMapper requestMapper;

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new RuntimeException("User already has a request for this event");
        }

        Request request = Request.builder()
                .requestor(user)
                .event(event)
                .status(RequestStatus.PENDING)
                .created(LocalDateTime.now())
                .build();

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestByUserId(Long userId) {
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Категории с id = " + userId + " не существует"));
    }

    private void validateNewRequest(Event event, Long userId, Long eventId) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidatetionConflict("Пользователь с id= " + userId + " не инициатор события");
        }
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new ValidatetionConflict("Превышен лимит участников события");
        }
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ValidatetionConflict("Событие не опубликовано");
        }
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ValidatetionConflict("Попытка добаления дубликата");
        }
    }
}
