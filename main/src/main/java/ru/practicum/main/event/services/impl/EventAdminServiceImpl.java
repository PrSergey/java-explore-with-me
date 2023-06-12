package ru.practicum.main.event.services.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.srorage.CategoryRepository;
import ru.practicum.main.constant.EventState;
import ru.practicum.main.constant.EventStateAction;
import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.dto.EventMapper;
import ru.practicum.main.event.dto.UpdateEventAdminRequestDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.QEvent;
import ru.practicum.main.event.services.EventAdminService;
import ru.practicum.main.event.storage.EventRepository;
import ru.practicum.main.event.storage.LocationRepository;
import ru.practicum.main.excepsion.ExistenceException;
import ru.practicum.main.excepsion.ValidationException;

import javax.validation.UnexpectedTypeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;

    @Override
    public List<EventDto> searchEvent(List<Long> users, List<EventState> states, List<Long> categories,
                                      String rangeStart, String rangeEnd, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from/size, size);
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (users != null){
            for (Long userId : users) {
                conditions.add(event.initiator.id.eq(userId));
            }
        }
        if (states != null) {
            for (EventState state : states) {
                conditions.add(event.state.stringValue().eq(state.toString()));
            }
        }
        if (categories != null){
            for (Long categoryId : categories) {
                conditions.add(event.category.id.eq(categoryId));
            }
        }
        if (rangeStart != null) {
            LocalDateTime rangeStartDate = LocalDateTime.parse(rangeStart,formatter);
            conditions.add(event.eventDate.after(rangeStartDate));
        }
        if (rangeEnd != null) {
            LocalDateTime rangeEndDate = LocalDateTime.parse(rangeEnd,formatter);
            conditions.add(event.eventDate.before(rangeEndDate));
        }
        if(conditions.size() != 0){
            BooleanExpression finalCondition = conditions.stream()
                    .reduce(BooleanExpression::and)
                    .get();
            return eventRepository.findAll(finalCondition, pageRequest)
                    .stream()
                    .map(eventMapper::toEventDto)
                    .collect(Collectors.toList());
        } else {
            return eventRepository.findAll(pageRequest).stream()
                    .map(eventMapper::toEventDto)
                    .collect(Collectors.toList());
        }

    }

    @Override
    public EventDto updateAdminByEvent(long eventId, UpdateEventAdminRequestDto eventDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ExistenceException("Event with id=" + eventId + " was not found"));
        LocalDateTime eventDate = event.getEventDate();
        if(eventDto.getEventDate() != null){
            eventDate = LocalDateTime.parse(eventDto.getEventDate(),formatter);
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new UnexpectedTypeException("Field: eventDate. " +
                        "Error: должно содеражть дату не раньше 2 часов от даты создания события. Value: " + eventDate);
            }
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ValidationException("Cannot publish the event because it's not in the right state:"
                    + event.getState());
        }

        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (eventDto.getStateAction().equals(EventStateAction.REJECT_EVENT)) {
                event.setState(EventState.CANCELED);
            }
        }
        eventMapper.updateAdmin(eventDto, event);
        if (eventDto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(eventDto.getCategory()).get());
        }
        if (eventDto.getLocation() != null) {
            locationRepository.save(eventDto.getLocation());
        }
        EventDto eventDtoAfterSave = eventMapper.toEventDto(eventRepository.save(event));
        eventDtoAfterSave.setEventDate(eventDate.format(formatter));
        return eventDtoAfterSave;
    }

}
