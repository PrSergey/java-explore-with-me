package ru.practicum.main.request.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.constant.EventState;
import ru.practicum.main.constant.RequestStatus;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.storage.EventRepository;
import ru.practicum.main.excepsion.ExistenceException;
import ru.practicum.main.excepsion.ValidationException;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.dto.RequestMapper;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.storage.RequestRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestPrivateServiceImpl implements RequestPrivateService {

    RequestRepository requestRepository;

    UserRepository userRepository;

    EventRepository eventRepository;

    RequestMapper requestMapper;

    @Override
    public ParticipationRequestDto save(long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ExistenceException("Event with id=" + eventId + " was not found."));
        if(requestRepository.existsByRequester_IdAndEvent_id(userId, eventId)) {
            throw new ValidationException("You cannot add a repeat request.");
        }
        if(event.getInitiator().getId() == userId) {
            throw new ValidationException("The initiator of the event cannot " +
                    "add a request to participate in his event.");
        }
        if(!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("You cannot participate in an unpublished event.");
        }
        long countRequestByEvent = requestRepository.countByEvent_id(eventId);
        if(event.getParticipantLimit() != 0 && countRequestByEvent > event.getParticipantLimit()) {
            throw new ValidationException("The limit of requests for participation in the event has been reached.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User with id" + userId + "was not found."));
        ParticipationRequest requestByEvent = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .build();
        if(!event.getRequestModeration()){
            requestByEvent.setStatus(RequestStatus.CONFIRMED);
        } else {
            requestByEvent.setStatus(RequestStatus.PENDING);
        }

        return requestMapper.toParticipationRequestDto(requestRepository.save(requestByEvent));
    }

    @Override
    public List<ParticipationRequestDto> getRequestByUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User with id" + userId + "was not found."));
        return requestRepository.findAllByRequester_Id(userId)
                .stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequestByUser(long userId, long requestId) {
        ParticipationRequest participationRequest = requestRepository.findById(requestId)
                .orElseThrow(()
                        -> new ValidationException("Participation request with id=" + requestId + "was not found."));
        participationRequest.setStatus(RequestStatus.PENDING);
        return requestMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }
}
