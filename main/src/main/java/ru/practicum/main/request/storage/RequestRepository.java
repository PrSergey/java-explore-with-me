package ru.practicum.main.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.constant.RequestStatus;
import ru.practicum.main.request.model.ParticipationRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    boolean existsByRequester_IdAndEvent_id(long userId, long eventId);

    long countByEvent_idAndStatus(long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByRequester_Id(long userId);

    List<ParticipationRequest> findAllByEvent_IdAndStatus(long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEvent_Id(long eventId);

    List<ParticipationRequest> findAllByIdIn(List<Long> requestIds);

    Long countAllByEvent_Id(Long eventId);

}
