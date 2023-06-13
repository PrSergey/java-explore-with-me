package ru.practicum.main.event.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.srorage.CategoryRepository;
import ru.practicum.main.constant.EventState;
import ru.practicum.main.constant.EventStateAction;
import ru.practicum.main.constant.RequestStatus;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.services.EventPrivateService;
import ru.practicum.main.event.storage.EventRepository;

import ru.practicum.main.event.storage.LocationRepository;
import ru.practicum.main.excepsion.ExistenceException;
import ru.practicum.main.excepsion.ValidationException;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.dto.RequestMapper;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.storage.RequestRepository;
import ru.practicum.main.user.storage.UserRepository;

import javax.validation.UnexpectedTypeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventPrivateServiceImpl implements EventPrivateService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final EventMapper eventMapper;

    private final RequestRepository requestRepository;

    private final RequestMapper requestMapper;

    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;


    @Override
    @Transactional
    public EventDto saveEvent(long userId, NewEventDto newEventDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(),formatter);
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))){
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
        PageRequest pageRequest = PageRequest.of(from/size, size);
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, pageRequest);
        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
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
        if (updateEvent.getEventDate() != null){
            eventDate = LocalDateTime.parse(updateEvent.getEventDate(),formatter);
            if(eventDate.isBefore(LocalDateTime.now())) {
                throw new UnexpectedTypeException("Field: eventDate. " +
                        "Error: должно содеражть дату не раньше 2 часов от даты создания события. Value: "
                        + updateEvent.getEventDate());
            }
            event.setEventDate(eventDate);
        }
        if(event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Нельзя изменять событие, когда оно опубликовано.");
        }

        checkInitiatorEventIsEqualsUserReq(event, userId);
        if(updateEvent.getStateAction() != null){
            if(updateEvent.getStateAction().equals(EventStateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else if(updateEvent.getStateAction().equals(EventStateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            } else if(updateEvent.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
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
        List<ParticipationRequest> allRequestById= requestRepository.findAllByEvent_Id(eventId);
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
        if (requestsByEvent == null){
            throw new ValidationException("RequestIds was not found in your request");
        }
        List<ParticipationRequest> requestsByIds = requestRepository.findAllByIdIn(requestsByEvent.getRequestIds());
        checkInitiatorEventIsEqualsUserReq(event, userId);
        long countApproveReq = requestRepository.countByEvent_idAndStatus(eventId, RequestStatus.CONFIRMED);
        checkForUpdateEventRequest(eventId, event, requestsByEvent, requestsByIds, countApproveReq);

        requestsByIds.forEach(req -> req.setStatus(requestsByEvent.getStatus()));
        List<ParticipationRequest> requests = requestRepository.saveAll(requestsByIds);
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();
        if(countApproveReq + requestsByEvent.getRequestIds().size() == event.getParticipantLimit()
                && requestsByEvent.getStatus().equals(RequestStatus.CONFIRMED)) {
            rejectedRequests = rejectedRequestsByEvent(eventId);
        }
        rejectedRequests.addAll(requestRepository.findAllByEvent_IdAndStatus(eventId, RequestStatus.REJECTED));
        updateConfirmedRequestsByEvent(event, requestsByEvent);
        return makeResponseForUpdateRequest(requests, rejectedRequests, requestsByEvent);
    }

    private void updateConfirmedRequestsByEvent(Event event, EventRequestStatusUpdateRequestDto requestsByEvent) {
        if(requestsByEvent.getStatus().equals(RequestStatus.CONFIRMED)) {
            long confirmedRequests = event.getConfirmedRequests();
            event.setConfirmedRequests(confirmedRequests + requestsByEvent.getRequestIds().size());
            eventRepository.save(event);
        }
    }

    private EventRequestStatusUpdateResultDto makeResponseForUpdateRequest(List<ParticipationRequest> requests,
                                                                           List<ParticipationRequest> rejectedRequests,
                                                                 EventRequestStatusUpdateRequestDto requestsByEvent) {
        EventRequestStatusUpdateResultDto resultDto = new EventRequestStatusUpdateResultDto();
        if(requestsByEvent.getStatus().equals(RequestStatus.CONFIRMED)) {
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
        if(countApproveReq == event.getParticipantLimit()
                && requestsByEvent.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ValidationException("The participant limit has been reached");
        } else if (countApproveReq + requestsByEvent.getRequestIds().size() > event.getParticipantLimit()
                && requestsByEvent.getStatus().equals(RequestStatus.CONFIRMED) ) {
            throw new ValidationException("The participant limit has been reached");
        }
        for(ParticipationRequest request: requestsByIds) {
            if(!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ValidationException("Request must have status PENDING");
            }
        }
    }

    private void checkInitiatorEventIsEqualsUserReq(Event event, long userId) {
        if(event.getInitiator().getId() != userId) {
            throw new ValidationException("The user with the id="
                    + userId + " is not the initiator of the event with the id = " + event.getId());
        }
    }
}
