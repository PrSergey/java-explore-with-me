package ru.practicum.main;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.stats.StatsClient;

@Configuration
public class MainConfig {

    @Bean
    public void makeStatsClient() {
        new StatsClient();
    }

}
