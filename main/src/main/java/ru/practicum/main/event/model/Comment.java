package ru.practicum.main.event.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.constant.CommentStatus;
import ru.practicum.main.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "event_id")
    @ManyToOne
    private Event event;

    @Column(name = "text")
    private String text;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User creator;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CommentStatus status;

    @Column(name = "created")
    private LocalDateTime created;

}
