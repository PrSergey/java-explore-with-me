package ru.practicum.stats.service;

import ru.practicum.stats.EndpointHitDto;
import ru.practicum.stats.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    EndpointHitDto save(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> get(List<String> uris, Boolean unique, LocalDateTime start, LocalDateTime end);
}
