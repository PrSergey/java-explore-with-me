package ru.practicum.main.event.controllers;


import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.services.EventPrivateService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@Valid
public class EventPrivateController {

    EventPrivateService eventPrivateService;

    @PostMapping
    public EventDto saveEvent(@PathVariable long userId,
                              @RequestBody NewEventDto newEventDto) {
        log.info("POST - запрос на добавление эвента");
        return eventPrivateService.saveEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getAllEventByInitiatorId(@PathVariable long userId,
                                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GET - запрос на получение всех событий пользователя с id={}", userId);
        return eventPrivateService.getAllEventByInitiatorId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventById(@PathVariable long userId,
                                 @PathVariable long eventId) {
        log.info("GET - запрос на получение эвента с id={} пользователем с id={}", eventId, userId);
        return eventPrivateService.getEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEvent(@PathVariable long userId,
                                @PathVariable long eventId,
                                @RequestBody UpdateEventUserRequest updateEvent) {
        log.info("PATCH - запрос пользователем с id={} а обновление эвента с id={}", userId, eventId);
        return eventPrivateService.updateEvent(userId, eventId,updateEvent);
    }


}
