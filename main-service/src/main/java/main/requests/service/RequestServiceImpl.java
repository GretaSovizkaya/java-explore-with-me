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

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = checkUser(userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id= " + eventId + " не найдено"));
        LocalDateTime createdOn = LocalDateTime.now();
        validateNewRequest(event, userId, eventId);
        Request request = new Request();
        request.setCreated(createdOn);
        request.setRequester(user);
        request.setEvent(event);

        if (event.isRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        requestRepository.save(request);

        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return RequestMapper.toParticipationRequestDto(request);
    }


    @Override
    public List<ParticipationRequestDto> getRequestByUserId(Long userId) {
        checkUser(userId);
        List<Request> result = requestRepository.findAllByRequesterId(userId);
        return result.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        checkUser(userId);
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(
                () -> new NotFoundException("Запрос с id= " + requestId + " не найден"));
        if (request.getStatus().equals(RequestStatus.CANCELED) || request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new ValidatetionConflict("Запрос не подтвержден");
        }
        request.setStatus(RequestStatus.CANCELED);
        Request requestAfterSave = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(requestAfterSave);
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
