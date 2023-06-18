package ru.practicum.main.event.services.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.srorage.CategoryRepository;
import ru.practicum.main.constant.*;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.QEvent;
import ru.practicum.main.event.services.EventService;
import ru.practicum.main.event.storage.EventRepository;
import ru.practicum.main.event.storage.LocationRepository;
import ru.practicum.main.excepsion.ExistenceException;
import ru.practicum.main.excepsion.ValidationException;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.dto.RequestMapper;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.storage.RequestRepository;
import ru.practicum.main.user.storage.UserRepository;
import ru.practicum.main.stats.StatsClient;
import ru.practicum.stats.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.UnexpectedTypeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final EventMapper eventMapper;

    private final RequestRepository requestRepository;

    private final RequestMapper requestMapper;

    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;

    private final StatsClient statsClient;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getEvent(String text, List<Long> categories, Boolean paid, String rangeStart,
                                   String rangeEnd, Boolean onlyAvailable, EventSort sort, int from, int size,
                                   HttpServletRequest request) {
        sendEndpointInStats(request);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (text == null && categories == null && paid == null && rangeStart == null && rangeEnd == null) {
            return new ArrayList<>();
        }
        BooleanExpression finalCondition = makeBooleanExpressionForGet(text, categories, paid, rangeStart, rangeEnd);
        List<Event> events = eventRepository.findAll(finalCondition, pageRequest).stream().collect(Collectors.toList());

        if (onlyAvailable) {
            events.stream()
                    .filter(ev -> (ev.getParticipantLimit() - ev.getConfirmedRequests()) > 0)
                    .collect(Collectors.toList());
        }

        List<EventDto> eventDtos = new ArrayList<>();
        for (Event event: events) {
            EventDto eventDto = eventMapper.toEventDto(event);
            eventDto.setViews(setViewsInEventDto(event));
            eventDtos.add(eventDto);
        }
        if (sort != null && sort.equals(EventSort.VIEWS)) {
            eventDtos.stream().sorted(Comparator.comparing(EventDto::getViews)).collect(Collectors.toList());
        } else if (sort != null && sort.equals(EventSort.EVENT_DATE)) {
            eventDtos.stream().sorted(Comparator.comparing(EventDto::getEventDate)).collect(Collectors.toList());
        }
        return eventDtos;
    }

    private BooleanExpression makeBooleanExpressionForGet(String text, List<Long> categories, Boolean paid, String rangeStart,
                                                          String rangeEnd) {
        List<BooleanExpression> conditions = new ArrayList<>();
        QEvent event = QEvent.event;
        if (rangeEnd != null && rangeStart != null
                && LocalDateTime.parse(rangeStart, formatter).isAfter(LocalDateTime.parse(rangeEnd, formatter))) {
            throw new UnexpectedTypeException("The beginning of the range cannot be later than the end of the range.");
        }
        if (text != null) {
            List<BooleanExpression> textInAnnotationOrDescription = new ArrayList<>();
            String textLowerCase = text.toLowerCase();
            textInAnnotationOrDescription.add(event.annotation.toLowerCase().like("%" + textLowerCase + "%"));
            textInAnnotationOrDescription.add(event.description.toLowerCase().like("%" + textLowerCase + "%"));
            BooleanExpression booleanExpressionText = textInAnnotationOrDescription.stream()
                    .reduce(BooleanExpression::or)
                    .get();
            conditions.add(booleanExpressionText);
        }
        if (categories != null)
            for (Long catId: categories) {
                conditions.add(event.category.id.eq(catId));
            }
        if (paid != null)
            conditions.add(event.paid.eq(paid));
        if (rangeStart != null) {
            LocalDateTime rangeStartDate = LocalDateTime.parse(rangeStart, formatter);
            conditions.add(event.eventDate.after(rangeStartDate));
        } else {
            conditions.add(event.eventDate.after(LocalDateTime.now()));
        }
        if (rangeEnd != null) {
            LocalDateTime rangeEndDate = LocalDateTime.parse(rangeEnd, formatter);
            conditions.add(event.eventDate.before(rangeEndDate));
        }
        return conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEventById(long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ExistenceException("Event with id=" + eventId + "was not found"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ExistenceException("Event was not found or not published");
        }
        EventDto eventDto = eventMapper.toEventDto(eventRepository.save(event));
        eventDto.setViews(setViewsInEventDto(event));
        sendEndpointInStats(request);
        return eventDto;
    }

    private long setViewsInEventDto(Event event) {
        long views;
        List<String> uri = List.of("/events/" + event.getId());
        List<ViewStatsDto> viewStats = statsClient.getViewStats(event.getCreatedOn().format(formatter),
                LocalDateTime.now().format(formatter), uri, false);
        if (viewStats.isEmpty()) {
            return 0;
        } else {
            views = viewStats.size();
        }
        return views;
    }

    private void sendEndpointInStats(HttpServletRequest request) {
        String app = AppConstant.NAME_SERVICE;
        String ipUser = request.getRemoteAddr();
        String requestURI = request.getRequestURI();
        statsClient.saveEndpointHit(app, requestURI, ipUser);
    }

    @Override
    @Transactional
    public List<EventDto> searchEvent(List<Long> users, List<EventState> states, List<Long> categories,
                                      String rangeStart, String rangeEnd, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<BooleanExpression> conditions = makeBooleanExpression(users, states, categories, rangeStart, rangeEnd);
        List<Event> events;
        if (conditions.size() != 0) {
            BooleanExpression finalCondition = conditions.stream()
                    .reduce(BooleanExpression::and)
                    .get();
            events = eventRepository.findAll(finalCondition, pageRequest).stream().collect(Collectors.toList());
        } else {
            events = eventRepository.findAll(pageRequest).stream().collect(Collectors.toList());
        }
        List<EventDto> eventDtos = new ArrayList<>();
        for (Event event: events) {
            EventDto eventDto = eventMapper.toEventDto(event);
            eventDto.setViews(setViewsInEventDto(event));
            eventDtos.add(eventDto);
        }
        return eventDtos;
    }

    private List<BooleanExpression>  makeBooleanExpression(List<Long> users, List<EventState> states, List<Long> categories,
                                                    String rangeStart, String rangeEnd) {
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (users != null) {
            for (Long userId : users) {
                conditions.add(event.initiator.id.eq(userId));
            }
        }
        if (states != null) {
            for (EventState state : states) {
                conditions.add(event.state.stringValue().eq(state.toString()));
            }
        }
        if (categories != null) {
            for (Long categoryId : categories) {
                conditions.add(event.category.id.eq(categoryId));
            }
        }
        if (rangeStart != null) {
            LocalDateTime rangeStartDate = LocalDateTime.parse(rangeStart,formatter);
            conditions.add(event.eventDate.after(rangeStartDate));
        }
        if (rangeEnd != null) {
            LocalDateTime rangeEndDate = LocalDateTime.parse(rangeEnd,formatter);
            conditions.add(event.eventDate.before(rangeEndDate));
        }
        return conditions;
    }

    @Override
    @Transactional
    public EventDto updateAdminByEvent(long eventId, UpdateEventAdminRequestDto eventDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ExistenceException("Event with id=" + eventId + " was not found"));
        LocalDateTime eventDate = event.getEventDate();
        if (eventDto.getEventDate() != null) {
            eventDate = LocalDateTime.parse(eventDto.getEventDate(),formatter);
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new UnexpectedTypeException("Field: eventDate. " +
                        "Error: должно содеражть дату не раньше 2 часов от даты создания события. Value: " + eventDate);
            }
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ValidationException("Cannot publish the event because it's not in the right state:"
                    + event.getState());
        }

        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (eventDto.getStateAction().equals(EventStateAction.REJECT_EVENT)) {
                event.setState(EventState.CANCELED);
            }
        }
        eventMapper.updateAdmin(eventDto, event);
        if (eventDto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(eventDto.getCategory()).get());
        }
        if (eventDto.getLocation() != null) {
            locationRepository.save(eventDto.getLocation());
        }
        EventDto eventDtoAfterSave = eventMapper.toEventDto(eventRepository.save(event));
        eventDtoAfterSave.setEventDate(eventDate.format(formatter));
        eventDtoAfterSave.setViews(setViewsInEventDto(event));
        return eventDtoAfterSave;
    }

    @Override
    @Transactional
    public EventDto saveEvent(long userId, NewEventDto newEventDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(),formatter);
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new UnexpectedTypeException("Field: eventDate. " +
                    "Error: должно содеражть дату не раньше 2 часов от даты создания события. Value: " + eventDate);
        }

        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }

        Event event = eventMapper.toEventFromNewEventDto(newEventDto);
        event.setLocation(locationRepository.save(newEventDto.getLocation()));
        event.setEventDate(eventDate);
        event.setCategory(categoryRepository.findById(newEventDto.getCategory()).get());
        event.setInitiator(userRepository.findById(userId).get());
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        EventDto eventDto = eventMapper.toEventDto(eventRepository.save(event));
        eventDto.setEventDate(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(eventDate));
        return eventDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllEventByInitiatorId(long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, pageRequest);
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event: events) {
            EventShortDto eventShortDto = eventMapper.toEventShortDto(event);
            eventShortDto.setViews(setViewsInEventDto(event));
            eventShortDtos.add(eventShortDto);
        }
        return eventShortDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEventById(long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ExistenceException("Event with id=" + eventId + " was not found"));
        checkInitiatorEventIsEqualsUserReq(event, userId);
        return eventMapper.toEventDto(event);
    }

    @Override
    @Transactional
    public EventDto updateEvent(long userId, long eventId, UpdateEventUserRequestDto updateEvent) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ValidationException("Событие с id=" + eventId + " не найдено в базе."));
        if (updateEvent == null) {
            return eventMapper.toEventDto(event);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime eventDate;
        if (updateEvent.getEventDate() != null) {
            eventDate = LocalDateTime.parse(updateEvent.getEventDate(),formatter);
            if (eventDate.isBefore(LocalDateTime.now())) {
                throw new UnexpectedTypeException("Field: eventDate. " +
                        "Error: должно содеражть дату не раньше 2 часов от даты создания события. Value: "
                        + updateEvent.getEventDate());
            }
            event.setEventDate(eventDate);
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Нельзя изменять событие, когда оно опубликовано.");
        }

        checkInitiatorEventIsEqualsUserReq(event, userId);
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(EventStateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else if (updateEvent.getStateAction().equals(EventStateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            } else if (updateEvent.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
            }
        }

        if (updateEvent.getLocation() != null) {
            locationRepository.save(updateEvent.getLocation());
        }
        eventMapper.updateEvent(updateEvent, event);
        if (updateEvent.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEvent.getCategory()).get());
        }
        return eventMapper.toEventDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestByEvent(long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ExistenceException("Event with id=" + eventId + " was not found"));
        checkInitiatorEventIsEqualsUserReq(event, userId);
        List<ParticipationRequest> allRequestById = requestRepository.findAllByEvent_Id(eventId);
        return allRequestById
                .stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResultDto updateEventRequest(long userId, long eventId,
                                                                EventRequestStatusUpdateRequestDto requestsByEvent) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ValidationException("Событие с id=" + eventId + " не найдено в базе."));
        if (event.getConfirmedRequests() == event.getParticipantLimit()) {
            throw new ValidationException("The limit has been reached for the event.");
        }
        if (requestsByEvent == null) {
            throw new ValidationException("RequestIds was not found in your request");
        }
        List<ParticipationRequest> requestsByIds = requestRepository.findAllByIdIn(requestsByEvent.getRequestIds());
        checkInitiatorEventIsEqualsUserReq(event, userId);
        long countApproveReq = requestRepository.countByEvent_idAndStatus(eventId, RequestStatus.CONFIRMED);
        checkForUpdateEventRequest(eventId, event, requestsByEvent, requestsByIds, countApproveReq);

        requestsByIds.forEach(req -> req.setStatus(requestsByEvent.getStatus()));
        List<ParticipationRequest> requests = requestRepository.saveAll(requestsByIds);
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();
        if (countApproveReq + requestsByEvent.getRequestIds().size() == event.getParticipantLimit()
                && requestsByEvent.getStatus().equals(RequestStatus.CONFIRMED)) {
            rejectedRequests = rejectedRequestsByEvent(eventId);
        }
        rejectedRequests.addAll(requestRepository.findAllByEvent_IdAndStatus(eventId, RequestStatus.REJECTED));
        updateConfirmedRequestsByEvent(event, requestsByEvent);
        return makeResponseForUpdateRequest(requests, rejectedRequests, requestsByEvent);
    }

    private void updateConfirmedRequestsByEvent(Event event, EventRequestStatusUpdateRequestDto requestsByEvent) {
        if (requestsByEvent.getStatus().equals(RequestStatus.CONFIRMED)) {
            long confirmedRequests = event.getConfirmedRequests();
            event.setConfirmedRequests(confirmedRequests + requestsByEvent.getRequestIds().size());
            eventRepository.save(event);
        }
    }

    private EventRequestStatusUpdateResultDto makeResponseForUpdateRequest(List<ParticipationRequest> requests,
                                                                           List<ParticipationRequest> rejectedRequests,
                                                                           EventRequestStatusUpdateRequestDto requestsByEvent) {
        EventRequestStatusUpdateResultDto resultDto = new EventRequestStatusUpdateResultDto();
        if (requestsByEvent.getStatus().equals(RequestStatus.CONFIRMED)) {
            resultDto.setConfirmedRequests(requests
                    .stream()
                    .map(requestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList()));
        } else {
            resultDto.setRejectedRequests(requests
                    .stream()
                    .map(requestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList()));
        }

        resultDto.setRejectedRequests(rejectedRequests
                .stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList()));

        return resultDto;
    }

    private List<ParticipationRequest> rejectedRequestsByEvent(long eventId) {
        List<ParticipationRequest> requests = requestRepository
                .findAllByEvent_IdAndStatus(eventId, RequestStatus.PENDING);
        requests.forEach(req -> req.setStatus(RequestStatus.REJECTED));
        return requestRepository.saveAll(requests);
    }

    private void checkForUpdateEventRequest(long eventId, Event event,
                                            EventRequestStatusUpdateRequestDto requestsByEvent,
                                            List<ParticipationRequest> requestsByIds, long countApproveReq) {
        if (countApproveReq == event.getParticipantLimit()
                && requestsByEvent.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ValidationException("The participant limit has been reached");
        } else if (countApproveReq + requestsByEvent.getRequestIds().size() > event.getParticipantLimit()
                && requestsByEvent.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ValidationException("The participant limit has been reached");
        }
        for (ParticipationRequest request: requestsByIds) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ValidationException("Request must have status PENDING");
            }
        }
    }

    private void checkInitiatorEventIsEqualsUserReq(Event event, long userId) {
        if (event.getInitiator().getId() != userId) {
            throw new ValidationException("The user with the id="
                    + userId + " is not the initiator of the event with the id = " + event.getId());
        }
    }

}
