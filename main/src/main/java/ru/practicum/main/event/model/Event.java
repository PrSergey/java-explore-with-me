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
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "annotation")
    @NotBlank
    String annotation;

    @JoinColumn(name = "cat_id")
    @ManyToOne
    @NotNull
    Category category;

    @Column(name = "confirmed_requests")
    Integer confirmedRequests;

    @Column(name = "created_on")
    LocalDateTime createdOn;

    @Column(name = "description")
    String description;

    @Column(name = "event_date")
    @NotNull
    LocalDateTime eventDate;

    @JoinColumn(name = "initiator_id")
    @ManyToOne
    @NotNull
    User initiator;

    @JoinColumn(name = "loc_id")
    @ManyToOne
    @NotNull
    Location location;

    @Column(name = "paid")
    @NotNull
    Boolean paid;

    @Column(name = "participant_limit")
    Integer participantLimit;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Column(name = "state")
    EventState state;

    @Column(name = "title")
    @NotNull
    String title;

    @Column(name = "views")
    Long views;

}
