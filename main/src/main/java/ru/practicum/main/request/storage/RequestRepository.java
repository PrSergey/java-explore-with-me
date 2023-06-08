package ru.practicum.main.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.request.model.ParticipationRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    boolean existsByRequester_IdAndEvent_id(long userId, long eventId);

    long countByEvent_id(long eventId);

    List<ParticipationRequest> findAllByRequester_Id(long userId);


}
