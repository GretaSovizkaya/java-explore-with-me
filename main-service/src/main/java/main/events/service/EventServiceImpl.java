package main.events.service;

import client.StatsClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.StatDto;
import dto.StatResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import main.categories.model.Category;
import main.categories.repository.CategoryRepository;
import main.events.dto.*;
import main.events.mapper.EventMapper;
import main.events.model.Event;
import main.events.model.enums.EventAdminState;
import main.events.model.enums.EventStatus;
import main.events.repository.EventRepository;
import main.exceptions.*;
import main.location.mapper.LocationMapper;
import main.location.model.Location;
import main.location.repository.LocationRepository;
import main.requests.dto.ParticipationRequestDto;
import main.requests.mapper.RequestMapper;
import main.requests.model.Request;
import main.requests.model.RequestStatus;
import main.requests.repository.RequestRepository;
import main.users.model.User;
import main.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventServiceImpl implements EventService {
    final EventRepository eventRepository;
    final UserRepository userRepository;
    final CategoryRepository categoryRepository;
    final RequestRepository requestRepository;
    final LocationRepository locationRepository;
    final StatsClient statsClient;
    final ObjectMapper objectMapper;

    @Value("${server.application.name:ewm-service}")
    String applicationName;

    @Override
    public List<EventFullDto> getAllEventsAdmin(EventAdminParamsDto eventParamsAdmin) {
        PageRequest pageable = PageRequest.of(eventParamsAdmin.getFrom() / eventParamsAdmin.getSize(),
                eventParamsAdmin.getSize());

        Specification<Event> specification = Specification.where(null);

        List<Long> users = eventParamsAdmin.getUsers();
        List<String> states = eventParamsAdmin.getStates();
        List<Long> categories = eventParamsAdmin.getCategories();
        LocalDateTime rangeEnd = eventParamsAdmin.getRangeEnd();
        LocalDateTime rangeStart = eventParamsAdmin.getRangeStart();

        if (users != null && !users.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(users));
        }

        if (states != null && !states.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("eventStatus").as(String.class).in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        Page<Event> events = eventRepository.findAll(specification, pageable);

        List<EventFullDto> result = events.getContent().stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        Map<Long, List<Request>> confirmedRequestsCountMap = getConfirmedRequestsCount(events.toList());

        for (EventFullDto event : result) {
            List<Request> requests = confirmedRequestsCountMap.getOrDefault(event.getId(), List.of());
            event.setConfirmedRequests(requests.size());
        }

        return result;
    }

    @Override
    @Transactional
    public EventFullDto updateEventsAdmin(Long eventId, UpdateEventAdminRequestDto updateEvent) {
        Event oldEvent = checkEvent(eventId);

        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction() == EventAdminState.PUBLISH_EVENT && oldEvent.getEventStatus() != EventStatus.PENDING) {
                throw new ConflictStateException("Невозможно опубликовать событие, так как текущий статус не PENDING");
            }
            if (updateEvent.getStateAction() == EventAdminState.REJECT_EVENT && oldEvent.getEventStatus() == EventStatus.PUBLISHED) {
                throw new ConflictStateException("Нельзя отменить публикацию, так как событие уже опубликовано");
            }
        }

        Event eventForUpdate = eventUpdateBase(oldEvent, updateEvent);

        if (updateEvent.getEventDate() != null) {
            if (updateEvent.getEventDate().isBefore(LocalDateTime.now())) {
                throw new ConflictTimeException("Некорректные параметры даты. Дата начала события не может быть в прошлом.");
            }
            eventForUpdate.setEventDate(updateEvent.getEventDate());
        }

        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction() == EventAdminState.PUBLISH_EVENT) {
                eventForUpdate.setEventStatus(EventStatus.PUBLISHED);
            } else if (updateEvent.getStateAction() == EventAdminState.REJECT_EVENT) {
                eventForUpdate.setEventStatus(EventStatus.CANCELED);
            }
        }

        eventRepository.save(eventForUpdate);
        return EventMapper.toEventFullDto(eventForUpdate);
    }

    @Override
    @Transactional
    public EventFullDto updateEventsByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserRequestDto eventUpdate) {
        checkUser(userId);
        Event oldEvent = checkEvenByInitiatorAndEventId(userId, eventId);

        if (oldEvent.getEventStatus() == EventStatus.PUBLISHED) {
            throw new ConflictStateException("Изменить можно только неопубликованное событие");
        }

        if (eventUpdate.getEventDate() != null && eventUpdate.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictTimeException("Время не может быть раньше, чем через два часа от текущего момента");
        }

        Event eventForUpdate = eventUpdateBase(oldEvent, eventUpdate);

        if (eventUpdate.getStateAction() != null) {
            switch (eventUpdate.getStateAction()) {
                case SEND_TO_REVIEW:
                    eventForUpdate.setEventStatus(EventStatus.PENDING);
                    break;
                case CANCEL_REVIEW:
                    eventForUpdate.setEventStatus(EventStatus.CANCELED);
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный статус: " + eventUpdate.getStateAction());
            }
        }

        eventRepository.save(eventForUpdate);
        return EventMapper.toEventFullDto(eventForUpdate);
    }


    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id= " + userId + " не найден");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return eventRepository.findAll(pageRequest).getContent()
                .stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventsById(Long eventId, HttpServletRequest request) {
        Event event = checkEvent(eventId);

        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new NotFoundException("Событие с id = " + eventId + " не опубликовано");
        }

        statsClient.sendHit(StatDto.builder()
                .app(applicationName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        Map<Long, Long> viewStatsMap = getViews(List.of(event));
        Long views = viewStatsMap.getOrDefault(event.getId(), 1L);
        eventFullDto.setViews(views);
        return eventFullDto;
    }

    @Override
    public EventFullDto createEvents(Long userId, NewEventDto newEventDto) {
        LocalDateTime createdOn = LocalDateTime.now();
        User user = checkUser(userId);
        checkDateAndTime(LocalDateTime.now(), newEventDto.getEventDate());
        Category category = checkCategory(newEventDto.getCategory());
        Event event = EventMapper.toEvent(newEventDto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setEventStatus(EventStatus.PENDING);
        event.setCreatedDate(createdOn);

        if (newEventDto.getLocation() != null) {
            Location location = locationRepository.save(LocationMapper.toLocation(newEventDto.getLocation()));
            event.setLocation(location);
        }

        Event eventSaved = eventRepository.save(event);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventSaved);
        eventFullDto.setViews(0L);
        eventFullDto.setConfirmedRequests(0);
        return eventFullDto;
    }

    @Override
    public EventFullDto getEventsByUserIdAndEventId(Long userId, Long eventId) {
        checkUser(userId);
        Event event = checkEvenByInitiatorAndEventId(userId, eventId);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getAllParticipationRequestsByOwner(Long userId, Long eventId) {
        checkUser(userId);
        checkEvenByInitiatorAndEventId(userId, eventId);
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

    }

    @Override
    public List<EventShortDto> getAllEventsPublic(EventParamsDto eventParams, HttpServletRequest request) {
        if (eventParams.getRangeEnd() != null && eventParams.getRangeStart() != null) {
            if (eventParams.getRangeEnd().isBefore(eventParams.getRangeStart())) {
                throw new ValidationException("Дата окончания не может быть раньше даты начала");
            }
        }

        statsClient.sendHit(StatDto.builder()
                .app(applicationName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        Pageable pageable = PageRequest.of(eventParams.getFrom() / eventParams.getSize(), eventParams.getSize());

        Specification<Event> specification = Specification.where(null);
        LocalDateTime now = LocalDateTime.now();

        if (eventParams.getText() != null) {
            String searchText = eventParams.getText().toLowerCase();
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + searchText + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + searchText + "%")
                    ));
        }

        if (eventParams.getCategories() != null && !eventParams.getCategories().isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(eventParams.getCategories()));
        }

        LocalDateTime startDateTime = Objects.requireNonNullElse(eventParams.getRangeStart(), now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));

        if (eventParams.getRangeEnd() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), eventParams.getRangeEnd()));
        }

        if (eventParams.getOnlyAvailable() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("eventStatus"), EventStatus.PUBLISHED));

        List<Event> resultEvents = eventRepository.findAll(specification, pageable).getContent();
        List<EventShortDto> result = resultEvents
                .stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());

        Map<Long, Long> viewStatsMap = getViews(resultEvents);

        for (EventShortDto eventDto : result) {
            eventDto.setViews(viewStatsMap.getOrDefault(eventDto.getId(), 0L));
        }

        return result;
    }


    private List<Request> checkRequestOrEventList(Long eventId, List<Long> requestId) {
        return requestRepository.findByEventIdAndIdIn(eventId, requestId).orElseThrow(
                () -> new NotFoundException("Запроса с id = " + requestId + " или события с id = "
                        + eventId + "не существуют"));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = " + eventId + " не существует"));
    }

    private Category checkCategory(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Категории с id = " + catId + " не существует"));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с id = " + userId + " не существует"));
    }

    private Location checkLocation(Long locationId) {
        return locationRepository.findById(locationId).orElseThrow(
                () -> new NotFoundException("Локайшена с id = " + locationId + " не существует"));
    }

    private void checkDateAndTime(LocalDateTime time, LocalDateTime dateTime) {
        if (dateTime.isBefore(time.plusHours(2))) {
            throw new ValidationException("Поле должно содержать дату, которая еще не наступила.");
        }
    }

    private Event checkEvenByInitiatorAndEventId(Long userId, Long eventId) {
        return eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(
                () -> new NotFoundException("События с id = " + eventId + "и с пользователем с id = " + userId +
                        " не существует"));
    }

    private Map<Long, Long> getViews(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());

        List<LocalDateTime> startDates = events.stream()
                .map(Event::getCreatedDate)
                .collect(Collectors.toList());
        LocalDateTime earliestDate = startDates.stream()
                .min(LocalDateTime::compareTo)
                .orElse(null);
        Map<Long, Long> viewStatsMap = new HashMap<>();

        if (earliestDate != null) {
            ResponseEntity<Object> response = statsClient.getStats(
                    earliestDate.toString(),
                    LocalDateTime.now().toString(),
                    uris,
                    true
            );

            if (response.getBody() instanceof List<?>) {
                List<?> rawList = (List<?>) response.getBody();
                List<StatResponseDto> statOutDtoList = objectMapper.convertValue(rawList, new TypeReference<List<StatResponseDto>>() {
                });

                viewStatsMap = statOutDtoList.stream()
                        .filter(statsDto -> statsDto.getUri().startsWith("/events/"))
                        .collect(Collectors.toMap(
                                statsDto -> Long.parseLong(statsDto.getUri().substring("/events/".length())),
                                StatResponseDto::getHits
                        ));
            } else {
                log.warn("Ошибка десериализации: response.getBody() не является списком");
            }
        }
        return viewStatsMap;
    }

    private Map<Long, List<Request>> getConfirmedRequestsCount(List<Event> events) {

        List<Request> requests = requestRepository.findAllByEventIdInAndStatus(events
                .stream().map(Event::getId).collect(Collectors.toList()), RequestStatus.CONFIRMED);
        return requests.stream().collect(Collectors.groupingBy(r -> r.getEvent().getId()));
    }

    private List<Request> rejectRequest(List<Long> ids, Long eventId) {
        List<Request> rejectedRequests = new ArrayList<>();
        List<Request> requestList = new ArrayList<>();
        List<Request> requestListLoaded = checkRequestOrEventList(eventId, ids);

        for (Request request : requestListLoaded) {

            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                break;
            }

            request.setStatus(RequestStatus.REJECTED);
            requestList.add(request);
            rejectedRequests.add(request);
        }
        requestRepository.saveAll(requestList);
        return rejectedRequests;
    }

    private Event eventUpdateBase(Event event, UpdateEventBaseDto updateEvent) {

        if (updateEvent.getAnnotation() != null && !updateEvent.getAnnotation().isBlank()) {
            event.setAnnotation(updateEvent.getAnnotation());
        }

        Long gotCategory = updateEvent.getCategory();

        if (gotCategory != null) {
            Category category = checkCategory(gotCategory);
            event.setCategory(category);
        }

        if (updateEvent.getDescription() != null && !updateEvent.getDescription().isBlank()) {
            event.setDescription(updateEvent.getDescription());
        }

        if (updateEvent.getLocation() != null) {
            event.setLocation(LocationMapper.toLocation(updateEvent.getLocation()));
        }

        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }

        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }

        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }

        if (updateEvent.getTitle() != null && !updateEvent.getTitle().isBlank()) {
            event.setTitle(updateEvent.getTitle());
        }

        return event;
    }

    @Override
    public Map<String, List<ParticipationRequestDto>> approveRequests(final Long userId, final Long eventId,
                                                                      final EventRequestStatusUpdateRequestDto requestUpdateDto) {

        final User user = checkUser(userId);
        final Event event = checkEvent(eventId);

        if (!Objects.equals(event.getInitiator(), user)) {
            throw new ValidatetionConflict("Пользователь не является инициатором этого события.");
        }

        final List<Request> requests = requestRepository.findRequestByIdIn(requestUpdateDto.getRequestIds().stream().toList());

        if (event.getRequestModeration() && event.getParticipantLimit().equals(event.getConfirmedRequests()) &&
                event.getParticipantLimit() != 0 && requestUpdateDto.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ValidatetionConflict("Лимит заявок на участие в событии исчерпан.");
        }

        final boolean verified = requests.stream()
                .allMatch(request -> request.getEvent().getId().longValue() == eventId);
        if (!verified) {
            throw new ValidatetionConflict("Список запросов не относятся к одному событию.");
        }

        final Map<String, List<ParticipationRequestDto>> requestMap = new HashMap<>();

        if (requestUpdateDto.getStatus().equals(RequestStatus.REJECTED)) {
            if (requests.stream()
                    .anyMatch(request -> request.getStatus().equals(RequestStatus.CONFIRMED))) {
                throw new ValidatetionConflict("Запрос на установление статуса <ОТМЕНЕНА>. Подтвержденые заявки нельзя отменить.");
            }
            log.info("Запрос на отклонение заявки подтвержден.");

            requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            List<Request> savedRequests = requestRepository.saveAll(requests);
            List<ParticipationRequestDto> rejectedRequests = savedRequests.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .toList();
            requestMap.put("rejectedRequests", rejectedRequests);
        } else {
            if (requests.stream()
                    .anyMatch(request -> !request.getStatus().equals(RequestStatus.PENDING))) {
                throw new ValidatetionConflict("Запрос на установление статуса <ПОДТВЕРЖДЕНА>. Заявки должны быть со статусом <В ОЖИДАНИИ>.");
            }
            Integer confRequests = 0;

            if (event.getConfirmedRequests() != null) {
                confRequests = event.getConfirmedRequests();
            }

            long limit = event.getParticipantLimit() - confRequests;
            List<Request> confirmedList = requests.stream()
                    .limit(limit)
                    .peek(request -> request.setStatus(RequestStatus.CONFIRMED))
                    .toList();
            List<Request> savedConfirmedRequests = requestRepository.saveAll(confirmedList);
            log.info("Заявки на участие сохранены со статусом <ПОДТВЕРЖДЕНА>.");

            List<ParticipationRequestDto> confirmedRequests = savedConfirmedRequests.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .toList();
            requestMap.put("confirmedRequests", confirmedRequests);

            List<Request> rejectedList = requests.stream()
                    .skip(limit)
                    .peek(request -> request.setStatus(RequestStatus.REJECTED))
                    .toList();
            List<Request> savedRejectedRequests = requestRepository.saveAll(rejectedList);
            log.info("Часть заявок на участие сохранены со статусом <ОТМЕНЕНА>, в связи с превышением лимита.");
            List<ParticipationRequestDto> rejectedRequests = savedRejectedRequests.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .toList();
            requestMap.put("rejectedRequests", rejectedRequests);

            if (event.getConfirmedRequests() != null) {
                confRequests = event.getConfirmedRequests();
            }
            event.setConfirmedRequests(confirmedList.size() + confRequests);
            eventRepository.save(event);
        }
        return requestMap;
    }

}