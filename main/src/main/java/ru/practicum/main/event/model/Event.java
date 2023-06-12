package ru.practicum.main.event.model;

import lombok.*;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.constant.EventState;
import ru.practicum.main.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "annotation")
    @NotBlank
    private String annotation;

    @JoinColumn(name = "cat_id")
    @ManyToOne
    private Category category;

    @Column(name = "confirmed_requests")
    private long confirmedRequests;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "description")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @JoinColumn(name = "initiator_id")
    @ManyToOne
    private User initiator;

    @JoinColumn(name = "loc_id")
    @ManyToOne
    private Location location;

    @Column(name = "paid")
    private boolean paid;

    @Column(name = "participant_limit")
    private long participantLimit;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "state_event")
    private EventState state;

    @Column(name = "title")
    private String title;

    @Column(name = "views")
    private long views;

    public Event(String annotation, Category category, String description, LocalDateTime eventDate, Location location,
                 Boolean paid, Integer participantLimit, Boolean requestModeration, String title) {
        this.annotation = annotation;
        this.category = category;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.requestModeration = requestModeration;
        this.title = title;
    }
}