package ru.practicum.main.event.services.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.srorage.CategoryRepository;
import ru.practicum.main.constant.EventSort;
import ru.practicum.main.constant.EventState;
import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.dto.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.QEvent;
import ru.practicum.main.event.services.EventPublicService;
import ru.practicum.main.event.storage.EventRepository;
import ru.practicum.main.excepsion.ExistenceException;
import ru.practicum.stats.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EventPublicServiceImpl implements EventPublicService {

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    private final StatsClient statsClient;

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getEvent(String text, List<Long> categories, Boolean paid, String rangeStart,
                                   String rangeEnd, Boolean onlyAvailable, EventSort sort, int from, int size,
                                   HttpServletRequest request) {
        sendEndpointInStats(request);
        PageRequest pageRequest = PageRequest.of(from/size, size);
        if(text == null && categories == null && paid == null && rangeStart == null && rangeEnd == null){
            return new ArrayList<>();
        }
        BooleanExpression finalCondition = makeBooleanExpressionForGet(text, categories, paid, rangeStart, rangeEnd);
        List<Event> events = eventRepository.findAll(finalCondition, pageRequest).stream().collect(Collectors.toList());

        if (onlyAvailable) {
            events.stream()
                    .filter(ev -> (ev.getParticipantLimit()-ev.getConfirmedRequests()) > 0)
                    .collect(Collectors.toList());
        }
        if (sort != null && sort.equals(EventSort.VIEWS)) {
            events.stream().sorted(Comparator.comparing(Event::getViews)).collect(Collectors.toList());
        } else if (sort != null && sort.equals(EventSort.EVENT_DATE)) {
            events.stream().sorted(Comparator.comparing(Event::getEventDate)).collect(Collectors.toList());
        }

        return events.stream().map(eventMapper::toEventDto).collect(Collectors.toList());
    }

    private BooleanExpression makeBooleanExpressionForGet(String text, List<Long> categories, Boolean paid, String rangeStart,
                                                          String rangeEnd) {
        List<BooleanExpression> conditions = new ArrayList<>();
        QEvent event = QEvent.event;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (text != null) {
            List<BooleanExpression> textInAnnotationOrDescription = new ArrayList<>();
            String textLowerCase = text.toLowerCase();
            textInAnnotationOrDescription.add(event.annotation.toLowerCase().like("%" + textLowerCase + "%"));
            textInAnnotationOrDescription.add(event.description.toLowerCase().like("%" + textLowerCase + "%"));
            BooleanExpression booleanExpressionText = textInAnnotationOrDescription.stream()
                    .reduce(BooleanExpression::or)
                    .get();
            conditions.add(booleanExpressionText);
        }
        if (categories != null)
            for(Long catId: categories) {
                conditions.add(event.category.id.eq(catId));
            }
        if (paid != null)
            conditions.add(event.paid.eq(paid));
        if (rangeStart != null) {
            LocalDateTime rangeStartDate = LocalDateTime.parse(rangeStart,formatter);
            conditions.add(event.eventDate.after(rangeStartDate));
        } else {
            conditions.add(event.eventDate.after(LocalDateTime.now()));
        }
        if (rangeEnd != null) {
            LocalDateTime rangeEndDate = LocalDateTime.parse(rangeEnd,formatter);
            conditions.add(event.eventDate.before(rangeEndDate));
        }
        return conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEventById(long eventId, HttpServletRequest request) {
        sendEndpointInStats(request);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ExistenceException("Event with id=" + eventId + "was not found"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ExistenceException("Event was not found or not published");
        }
        event.setViews(event.getViews() + 1);
        return eventMapper.toEventDto(eventRepository.save(event));
    }

    private void sendEndpointInStats(HttpServletRequest request) {
        String app = "ewm-main-service";
        String ipUser = request.getRemoteAddr();
        String requestURI = request.getRequestURI();
        statsClient.saveEndpointHit(app, requestURI, ipUser);
    }

}
