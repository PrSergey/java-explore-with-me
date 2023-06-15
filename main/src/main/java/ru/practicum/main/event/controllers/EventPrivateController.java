package ru.practicum.main.event.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.services.EventService;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto saveEvent(@PathVariable long userId,
                              @RequestBody @Valid NewEventDto newEventDto) {
        log.info("POST private - запрос на добавление эвента");
        return eventService.saveEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getAllEventByInitiatorId(@PathVariable long userId,
                                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GET private - запрос на получение всех событий пользователя с id={}", userId);
        return eventService.getAllEventByInitiatorId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventById(@PathVariable long userId,
                                 @PathVariable long eventId) {
        log.info("GET private - запрос на получение эвента с id={} пользователем с id={}", eventId, userId);
        return eventService.getEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEvent(@PathVariable long userId,
                                @PathVariable long eventId,
                                @RequestBody @Valid  UpdateEventUserRequestDto updateEvent) {
        log.info("PATCH private - запрос пользователем с id={} на обновление эвента с id={}", userId, eventId);
        return eventService.updateEvent(userId, eventId,updateEvent);
    }

    @GetMapping("/{eventId}/requests")
    List<ParticipationRequestDto> getRequestByEvent(@PathVariable long userId,
                                                    @PathVariable long eventId) {
        log.info("GET private - запрос получение информации о запросах на участие в событии текущего пользователя");
        return eventService.getRequestByEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    EventRequestStatusUpdateResultDto updateEventRequest(@PathVariable long userId,
                                                                @PathVariable long eventId,
                                                                @RequestBody(required = false) @Valid
                                                                EventRequestStatusUpdateRequestDto requestsByEvent) {
        log.info("PATCH private - запрос изменение статуса (подтверждена, отменена) заявок на участие " +
                "в событии текущего пользователя");
        return eventService.updateEventRequest(userId, eventId, requestsByEvent);
    }

}
