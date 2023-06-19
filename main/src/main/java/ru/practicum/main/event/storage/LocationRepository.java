package ru.practicum.main.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.event.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
