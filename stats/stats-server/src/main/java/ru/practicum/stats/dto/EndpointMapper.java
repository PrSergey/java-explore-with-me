package ru.practicum.stats.dto;

import ru.practicum.stats.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;

import java.time.LocalDateTime;

public class EndpointMapper {

    public static EndpointHit fromEndpointHitDto(EndpointHitDto endpointHitDto) {
        LocalDateTime startTime = LocalDateTime.parse(endpointHitDto.getTimestamp());
        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(startTime)
                .build();
    }

    public static EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        return EndpointHitDto.builder()
                .app(endpointHit.getApp())
                .ip(endpointHit.getIp())
                .uri(endpointHit.getUri())
                .timestamp(endpointHit.getTimestamp().toString())
                .build();
    }

}