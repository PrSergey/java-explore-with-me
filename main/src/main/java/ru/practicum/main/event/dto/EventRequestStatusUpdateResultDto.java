package ru.practicum.main.event.dto;

import lombok.Data;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.List;

@Data
public class EventRequestStatusUpdateResultDto {

    private List<ParticipationRequestDto> confirmedRequests;

    private List<ParticipationRequestDto> rejectedRequests;

}
