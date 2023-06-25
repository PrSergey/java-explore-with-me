package ru.practicum.main.event.dto.events;

import lombok.Data;
import ru.practicum.main.constant.RequestStatus;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequestDto {

    private List<Long> requestIds;

    private RequestStatus status;

}
