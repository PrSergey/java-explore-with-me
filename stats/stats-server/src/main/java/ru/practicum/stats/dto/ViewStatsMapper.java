package ru.practicum.stats.dto;

import ru.practicum.stats.ViewStatsDto;
import ru.practicum.stats.model.ViewStats;

public class ViewStatsMapper {

    public static ViewStats fromViewStatsDto(ViewStatsDto viewStatsDto) {
        return ViewStats.builder()
                .app(viewStatsDto.getApp())
                .uri(viewStatsDto.getUri())
                .hits(viewStatsDto.getHits())
                .build();
    }

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }

}
