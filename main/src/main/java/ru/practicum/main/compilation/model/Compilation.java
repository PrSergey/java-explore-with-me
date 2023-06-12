package ru.practicum.main.compilation.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.main.event.model.Event;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@ToString
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToMany
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    List<Event> events;

    Boolean pinned;

    String title;

}
