package ru.practicum.main.event.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.constant.EventState;
import ru.practicum.main.event.dto.comment.CommentDto;
import ru.practicum.main.event.dto.events.EventDto;
import ru.practicum.main.event.dto.events.UpdateEventAdminRequestDto;
import ru.practicum.main.event.services.CommentAdminService;
import ru.practicum.main.event.services.EventAdminService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private final EventAdminService eventService;

    private final CommentAdminService commentAdminService;

    @GetMapping
    public List<EventDto> searchEvent(@RequestParam(required = false) List<Long> users,
                               @RequestParam(required = false) List<EventState> states,
                               @RequestParam(required = false) List<Long> categories,
                               @RequestParam(required = false) String rangeStart,
                               @RequestParam(required = false) String rangeEnd,
                               @RequestParam(defaultValue = "0") int from,
                               @RequestParam(defaultValue = "10") int size) {
        log.info("GET admin - запрос админ поиска события администратором");
        return eventService.searchEvent(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateAdminByEvent(@PathVariable long eventId,
                                @RequestBody @Valid UpdateEventAdminRequestDto eventDto) {
        log.info("PATCH admin - запрос админ редактирование данных события и его статуса (отклонение/публикация).");
        return eventService.updateAdminByEvent(eventId, eventDto);
    }

    @PatchMapping("/comments")
    public CommentDto blockedCommentById(@RequestParam(name = "comId") long comId) {
        log.info("PATCH admin - запрос на блокировку комментария с id={}", comId);
        return commentAdminService.blockerCommentById(comId);
    }

}
