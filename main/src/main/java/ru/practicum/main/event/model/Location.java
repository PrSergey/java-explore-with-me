package ru.practicum.main.event.model;

import lombok.*;

import javax.persistence.*;


@Entity
@Table(name = "location")
@ToString
@Getter
@Setter
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "lat")
    float lat;

    @Column(name = "lon")
    float lon;

}
