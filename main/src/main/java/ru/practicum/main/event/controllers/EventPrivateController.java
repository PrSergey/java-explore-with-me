package ru.practicum.main.event.controllers;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.services.EventPrivateService;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventPrivateService eventPrivateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto saveEvent(@PathVariable long userId,
                              @RequestBody @Valid NewEventDto newEventDto) {
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
                                @RequestBody @Valid  UpdateEventUserRequestDto updateEvent) {
        log.info("PATCH - запрос пользователем с id={} на обновление эвента с id={}", userId, eventId);
        return eventPrivateService.updateEvent(userId, eventId,updateEvent);
    }

    @GetMapping("/{eventId}/requests")
    List<ParticipationRequestDto> getRequestByEvent(@PathVariable long userId,
                                                    @PathVariable long eventId) {
        log.info("GET - запрос получение информации о запросах на участие в событии текущего пользователя");
        return eventPrivateService.getRequestByEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    EventRequestStatusUpdateResultDto updateEventRequest (@PathVariable long userId,
                                                                @PathVariable long eventId,
                                                                @RequestBody @Valid
                                                                EventRequestStatusUpdateRequestDto requestsByEvent) {
        log.info("PATCH - запрос изменение статуса (подтверждена, отменена) заявок на участие " +
                "в событии текущего пользователя");
        return eventPrivateService.updateEventRequest(userId, eventId, requestsByEvent);
    }


}
