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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EndpointHit)) return false;
        EndpointHit that = (EndpointHit) o;
        return Objects.equals(id, that.id) && app.equals(that.app)
                && uri.equals(that.uri) && ip.equals(that.ip)
                && timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
