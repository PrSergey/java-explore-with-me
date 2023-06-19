package ru.practicum.main.request.controllers;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.services.RequestPrivateService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@AllArgsConstructor
public class RequestPrivateController {

    RequestPrivateService requestPrivateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto saveRequest(@PathVariable long userId,
                                        @RequestParam long eventId) {
        log.info("POST private - запрос от пользователя с id={} добавления заявки на участие в событии с id={}.",
                userId, eventId);
        return requestPrivateService.save(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getRequestByUser(@PathVariable long userId) {
        log.info("GET private - запрос от пользователя с id={} на получение заявок на свои участия в событиях.",
                userId);
        return requestPrivateService.getRequestByUser(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestByUser(@PathVariable long userId,
                                                @PathVariable long requestId) {
        log.info("PATCH private  - запрос от пользователя с id={} на отмечу своей заявки с id={} на участик в событие.",
                userId, requestId);
        return requestPrivateService.cancelRequestByUser(userId, requestId);
    }

}
