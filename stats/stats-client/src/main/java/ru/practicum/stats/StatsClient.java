package ru.practicum.stats;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class StatsClient {

    @Value("${stats.uri}")
    private String uri;

    @Autowired
    private final WebClient webClient;


    public EndpointHitDto saveEndpointHit(String app, String uriEndpoint, String ip) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(app)
                .ip(ip)
                .uri(uriEndpoint)
                .build();

        return webClient.post()
                .uri(uri+"/hit")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(endpointHitDto)
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
                .retrieve()
                .bodyToFlux(ViewStatsDto.class)
                .collectList()
                .block();
    }

}
