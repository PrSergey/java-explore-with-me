package ru.practicum.main.event.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.comment.CommentDto;
import ru.practicum.main.event.dto.comment.NewCommentDto;
import ru.practicum.main.event.dto.comment.UpdateCommentUserRequestDto;
import ru.practicum.main.event.dto.events.*;
import ru.practicum.main.event.services.CommentPrivateService;
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

    private final CommentPrivateService commentPrivateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto saveEvent(@PathVariable long userId,
                              @RequestBody @Valid NewEventDto newEventDto) {
        log.info("POST private - запрос на добавление эвента");
        return eventPrivateService.saveEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getAllEventByInitiatorId(@PathVariable long userId,
                                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GET private - запрос на получение всех событий пользователя с id={}", userId);
        return eventPrivateService.getAllEventByInitiatorId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventById(@PathVariable long userId,
                                 @PathVariable long eventId) {
        log.info("GET private - запрос на получение эвента с id={} пользователем с id={}", eventId, userId);
        return eventPrivateService.getEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEvent(@PathVariable long userId,
                                @PathVariable long eventId,
                                @RequestBody @Valid UpdateEventUserRequestDto updateEvent) {
        log.info("PATCH private - запрос пользователем с id={} на обновление эвента с id={}", userId, eventId);
        return eventPrivateService.updateEvent(userId, eventId,updateEvent);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestByEvent(@PathVariable long userId,
                                                    @PathVariable long eventId) {
        log.info("GET private - запрос получение информации о запросах на участие в событии текущего пользователя");
        return eventPrivateService.getRequestByEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResultDto updateEventRequest(@PathVariable long userId,
                                                                @PathVariable long eventId,
                                                                @RequestBody(required = false) @Valid
                                                                EventRequestStatusUpdateRequestDto requestsByEvent) {
        log.info("PATCH private - запрос изменение статуса (подтверждена, отменена) заявок на участие " +
                "в событии текущего пользователя");
        return eventPrivateService.updateEventRequest(userId, eventId, requestsByEvent);
    }

    @PostMapping("/{eventId}/comments")
    public CommentDto saveCommentForEvent(@PathVariable long eventId,
                                          @PathVariable long userId,
                                          @RequestBody @Valid NewCommentDto commentDto) {
        log.info("POST private - запрос на сохранение комментария пользователя с id={} к событию с id={}",
                userId, eventId);
        return commentPrivateService.saveCommentForEvent(userId, eventId, commentDto);
    }

    @PatchMapping("/comments/{comId}")
    public CommentDto updateComment(
                                    @PathVariable long userId,
                                    @PathVariable long comId,
                                    @RequestBody @Valid UpdateCommentUserRequestDto updateComment) {
        log.info("PATCH private - запрос на обновление комментария с id={} пользователем с id={}", comId, userId);
        return commentPrivateService.updateComment(userId, comId, updateComment);
    }

    @DeleteMapping("/comments/{comId}")
    public void deleteComment(@PathVariable long userId,
                              @PathVariable long comId) {
        log.info("DELETE private - запрос на удаление комментария с id={} пользователем с id={}", comId, userId);
        commentPrivateService.deleteComment(userId, comId);
    }

    @GetMapping("/comments/{comId}")
    public CommentDto getCommentById(@PathVariable long userId,
                                     @PathVariable long comId) {
        log.info("GET private - запрос на получение комментария с id={} пользователем с id={}", comId, userId);
        return commentPrivateService.getCommentById(userId, comId);
    }

    @GetMapping("/comments")
    public List<CommentDto> getCommentByUser(@PathVariable long userId,
                                             @RequestParam(name = "from", defaultValue = "0") int from,
                                             @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GET private - запрос на получение всех комментариев пользователем с id={}", userId);
        return commentPrivateService.getCommentByUser(userId, from, size);
    }

}
