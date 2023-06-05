package ru.practicum.stats.model;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class ViewStats {

    String app;

    String uri;

    Long hits;

}
