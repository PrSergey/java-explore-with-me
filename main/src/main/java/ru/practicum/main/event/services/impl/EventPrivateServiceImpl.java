package ru.practicum.main.event.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.services.EventPrivateService;
import ru.practicum.main.event.storage.EventRepository;

import ru.practicum.main.excepsion.ExistenceException;
import ru.practicum.main.excepsion.ValidationException;
import ru.practicum.main.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventPrivateServiceImpl implements EventPrivateService {

    EventRepository eventRepository;

    UserRepository userRepository;

    EventMapper eventMapper;

    @Override
    public EventDto saveEvent(long userId, NewEventDto newEventDto) {
        LocalDateTime eventDate = newEventDto.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))){
            throw new ValidationException("Field: eventDate. " +
                    "Error: должно содеражть дату не раньше 2 часов от даты создания события. Value: " + eventDate);
        }

        Event event = eventMapper.toEventFromNewEventDto(newEventDto);
        event.setInitiator(userRepository.findById(userId).get());
        event.setCreatedOn(LocalDateTime.now());

        return eventMapper.toEventDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getAllEventByInitiatorId(long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from/size, size);
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, pageRequest);
        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventDto getEventById(long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ExistenceException("Event with id=" + eventId + " was not found"));
        if(event.getInitiator().getId() != userId) {
            throw new ValidationException("Пользователь с id=" + userId
                    + " не является создателем события с id=" + eventId);
        }
        return eventMapper.toEventDto(event);
    }

    @Override
    public EventDto updateEvent(long userID, long eventId, UpdateEventUserRequest updateEvent) {
        return null;
    }

}