package ru.practicum.main.event.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.constant.EventSort;
import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.services.EventPublicService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventPublicController {

    private final EventPublicService eventPublicService;

    @GetMapping
    List<EventDto> getEvent(@RequestParam(required = false) String text,
                            @RequestParam(required = false) List<Long> categories,
                            @RequestParam(required = false) Boolean paid,
                            @RequestParam(required = false) String rangeStart,
                            @RequestParam(required = false) String rangeEnd,
                            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                            @RequestParam(required = false) EventSort sort,
                            @RequestParam(defaultValue = "0") int from,
                            @RequestParam(defaultValue = "10") int size,
                            HttpServletRequest request) {
        log.info("GET public - запрос пользователя на получение событий с возможностью фильтрации");
        return eventPublicService.getEvent(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    EventDto getEventById(@PathVariable long id, HttpServletRequest request) {
        log.info("GET public - запрос пользователя на получение подробной информации об опубликованном событии с id={}", id);
        return eventPublicService.getEventById(id, request);
    }

}
