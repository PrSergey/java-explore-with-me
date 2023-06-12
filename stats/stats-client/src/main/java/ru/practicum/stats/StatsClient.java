package ru.practicum.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsClient {

    @Autowired
    public StatsClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Value("${stats.uri}")
    private String uri;

    private final WebClient webClient;

    public EndpointHitDto saveEndpointHit(String app, String uriEndpoint, String ip) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(app)
                .ip(ip)
                .uri(uriEndpoint)
                .build();

        return webClient
                .post()
                .uri(uri+"/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(endpointHitDto))
                .retrieve()
                .bodyToMono(EndpointHitDto.class)
                .block();
    }

    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end,
                                           List<String> uries, Boolean unique) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(uri+"/stats")
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
