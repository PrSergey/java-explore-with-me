package ru.practicum.stats.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "endpoint_hits",  schema = "public")
@Getter
@Setter
@ToString
@Builder
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank
    @Column(name = "app")
    String app;

    @NotBlank
    @Column(name = "uri")
    String uri;

    @NotBlank
    @Column(name = "ip")
    String ip;

    @Column(name = "time_add")
    LocalDateTime timestamp;

}
