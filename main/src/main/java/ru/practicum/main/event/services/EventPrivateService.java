package ru.practicum.main.event.services;

import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventUserRequest;

import java.util.List;

public interface EventPrivateService {

    EventDto saveEvent(long userId, NewEventDto newEventDto);

    List<EventShortDto> getAllEventByInitiatorId(long userID, int from, int size);

    EventDto getEventById( long userId, long eventId);

    EventDto updateEvent(long userID, long eventId, UpdateEventUserRequest updateEvent);





}
