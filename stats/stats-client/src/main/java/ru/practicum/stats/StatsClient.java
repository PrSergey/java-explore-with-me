package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StatsClient {

    @Value("${stats.uri}")
    String uri;

    WebClient webClient;

    public EndpointHitDto saveEndpointHit(String app, String uri, String ip) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(app)
                .ip(ip)
                .uri(uri)
                .build();

        return webClient
                .post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHitDto, EndpointHitDto.class)
                .retrieve()
                .bodyToFlux(EndpointHitDto.class)
                .blockFirst();
    }

    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end,
                                           List<String> uries, Boolean unique) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(uri)
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uries", uries)
                        .queryParam("unique", unique)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsDto>>() {})
                .block();
    }

}
