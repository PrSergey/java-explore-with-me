package ru.practicum.main.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.stats.EndpointHitDto;
import ru.practicum.stats.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StatsClient {



    private final WebClient webClient;

    @Autowired
    public StatsClient(@Value("${stats.uri}") String uri) {
        this.webClient = WebClient.create(uri);
    }


    public EndpointHitDto saveEndpointHit(String app, String uriEndpoint, String ip) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(app)
                .ip(ip)
                .uri(uriEndpoint)
                .timestamp(LocalDateTime.now().format(formatter))
                .build();

        return webClient.post()
                .uri("/hit")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(endpointHitDto)
                .retrieve()
                .bodyToMono(EndpointHitDto.class)
                .block();
    }



    public List<ViewStatsDto> getViewStats(String start, String end,
                                           List<String> uries, Boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
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
