package ru.practicum.main.event.model;

import lombok.*;

import javax.persistence.*;


@Entity
@Table(name = "locations")
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "lat")
    private float lat;

    @Column(name = "lon")
    private float lon;

}
