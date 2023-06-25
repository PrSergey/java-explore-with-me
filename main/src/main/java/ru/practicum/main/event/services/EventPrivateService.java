package ru.practicum.main.event.services;

import ru.practicum.main.event.dto.events.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventPrivateService {

    EventDto saveEvent(long userId, NewEventDto newEventDto);

    List<EventShortDto> getAllEventByInitiatorId(long userID, int from, int size);

    EventDto getEventById(long userId, long eventId);

    EventDto updateEvent(long userId, long eventId, UpdateEventUserRequestDto updateEvent);

    List<ParticipationRequestDto> getRequestByEvent(long userId, long eventId);

    EventRequestStatusUpdateResultDto updateEventRequest(long userId, long eventId,
                                                         EventRequestStatusUpdateRequestDto requestsByEvent);



}
