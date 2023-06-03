package ru.practicum.stats.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.EndpointHitDto;
import ru.practicum.stats.ViewStatsDto;
import ru.practicum.stats.server.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
public class StatsServerController {

    StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto save(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Добавление в сервис статистики эндпоинта" + endpointHitDto);
        return statsService.save(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> get(@RequestParam(name = "start")
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                  @RequestParam (name = "end")
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                  @RequestParam (name = "uris", required = false) List<String> uris,
                                  @RequestParam (name = "unique", defaultValue = "false") boolean unique) {
        log.info("Запрос на получение статистики с временем старта {}, " +
                "временем окончания {}, списком uri {}, учет уникальных посещений {}", start, end, uris, unique);
        return statsService.get(uris, unique, start, end);
    }
}
