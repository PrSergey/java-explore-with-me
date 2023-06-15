package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsClient {

    @Value("${stats.uri}")
    private String uri;

    private final WebClient webClient;



    public EndpointHitDto saveEndpointHit(String app, String uriEndpoint, String ip) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(app)
                .ip(ip)
                .uri(uriEndpoint)
                .timestamp(LocalDateTime.now().format(formatter))
                .build();

        return webClient.post()
                .uri(uri + "/hit")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(endpointHitDto)
                .retrieve()
                .bodyToMono(EndpointHitDto.class)
                .block();
    }

    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end,
                                           List<String> uries, Boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(uri + "/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uries", uries)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToFlux(ViewStatsDto.class)
                .collectList()
                .block();
    }

}
