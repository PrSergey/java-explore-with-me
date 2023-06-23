package ru.practicum.main.event.services.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.constant.AppConstant;
import ru.practicum.main.constant.CommentStatus;
import ru.practicum.main.constant.EventSort;
import ru.practicum.main.constant.EventState;
import ru.practicum.main.event.dto.events.EventDto;
import ru.practicum.main.event.dto.events.EventMapper;
import ru.practicum.main.event.model.Comment;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.QEvent;
import ru.practicum.main.event.services.EventPublicService;
import ru.practicum.main.event.storage.CommentRepository;
import ru.practicum.main.event.storage.EventRepository;
import ru.practicum.main.excepsion.ExistenceException;
import ru.practicum.main.stats.StatsClient;
import ru.practicum.stats.EndpointHitDto;
import ru.practicum.stats.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.UnexpectedTypeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventPublicServiceImpl implements EventPublicService {

    private final EventRepository eventRepository;

    private final StatsClient statsClient;

    private final CommentRepository commentRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventMapper eventMapper;

    @Override
    @Transactional
    public List<EventDto> getEvent(String text, List<Long> categories, Boolean paid, String rangeStart,
                                   String rangeEnd, Boolean onlyAvailable, EventSort sort, int from, int size,
                                   HttpServletRequest request) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        saveEndpointInStats(request);
        if (text == null && categories == null && paid == null && rangeStart == null && rangeEnd == null) {
            return new ArrayList<>();
        }
        BooleanExpression finalCondition = makeBooleanExpressionForGet(text, categories, paid, rangeStart, rangeEnd);
        List<Event> events = eventRepository.findAll(finalCondition, pageRequest).stream().collect(Collectors.toList());

        if (onlyAvailable) {
            events.stream()
                    .filter(ev -> (ev.getParticipantLimit() - ev.getConfirmedRequests()) > 0)
                    .collect(Collectors.toList());
        }
        System.out.println("EVENT NAME" + request.getRequestURI());
        List<EventDto> eventDtos = new ArrayList<>();
        for (Event event: events) {
            EventDto eventDto = eventMapper.toEventDto(event);
            eventDto.setViews(setViewsInEventDto(event));
            eventDtos.add(eventDto);
        }
        if (sort != null && sort.equals(EventSort.VIEWS)) {
            eventDtos.stream().sorted(Comparator.comparing(EventDto::getViews)).collect(Collectors.toList());
        } else if (sort != null && sort.equals(EventSort.EVENT_DATE)) {
            eventDtos.stream().sorted(Comparator.comparing(EventDto::getEventDate)).collect(Collectors.toList());
        }
        setCommentInEventDtos(eventDtos);
        return eventDtos;
    }

    private void setCommentInEventDtos(List<EventDto> event) {
        if (event == null || event.isEmpty()) {
            return;
        }
        List<Long> eventIds = event.stream().map(EventDto::getId).collect(Collectors.toList());
        List<Comment> comments = commentRepository.findAllByEvent_IdInAndStatus(eventIds, CommentStatus.PUBLISHED);
        Map<Long, EventDto> eventsMap = event.stream()
                .collect(Collectors.toMap(EventDto::getId, Function.identity()));
        for (Comment comment : comments) {
            List<Comment> commentByEvent = eventsMap.get(comment.getEvent().getId()).getComment();
            if (commentByEvent == null) {
                commentByEvent = new ArrayList<>();
            }
            commentByEvent.add(comment);
            eventsMap.get(comment.getEvent().getId()).setComment(commentByEvent);
        }
    }

    private BooleanExpression makeBooleanExpressionForGet(String text, List<Long> categories, Boolean paid, String rangeStart,
                                                          String rangeEnd) {
        List<BooleanExpression> conditions = new ArrayList<>();
        QEvent event = QEvent.event;
        if (rangeEnd != null && rangeStart != null
                && LocalDateTime.parse(rangeStart, formatter).isAfter(LocalDateTime.parse(rangeEnd, formatter))) {
            throw new UnexpectedTypeException("The beginning of the range cannot be later than the end of the range.");
        }
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
            for (Long catId: categories) {
                conditions.add(event.category.id.eq(catId));
            }
        if (paid != null)
            conditions.add(event.paid.eq(paid));
        if (rangeStart != null) {
            LocalDateTime rangeStartDate = LocalDateTime.parse(rangeStart, formatter);
            conditions.add(event.eventDate.after(rangeStartDate));
        } else {
            conditions.add(event.eventDate.after(LocalDateTime.now()));
        }
        if (rangeEnd != null) {
            LocalDateTime rangeEndDate = LocalDateTime.parse(rangeEnd, formatter);
            conditions.add(event.eventDate.before(rangeEndDate));
        }
        return conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    @Override
    @Transactional
    public EventDto getEventById(long eventId, HttpServletRequest request) {
        saveEndpointInStats(request);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ExistenceException("Event with id=" + eventId + "was not found"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ExistenceException("Event was not found or not published");
        }
        EventDto eventDto = eventMapper.toEventDto(eventRepository.save(event));
        eventDto.setViews(setViewsInEventDto(event));
        setCommentInEventDto(eventDto);
        return eventDto;
    }

    private void setCommentInEventDto(EventDto eventDto) {
        List<Comment> comments = commentRepository.findAllByEvent_IdAndStatus(eventDto.getId(),
                CommentStatus.PUBLISHED);
        eventDto.setComment(comments);
    }

    private EndpointHitDto saveEndpointInStats(HttpServletRequest request) {
        String app = AppConstant.NAME_SERVICE;
        String ipUser = request.getRemoteAddr();
        String requestURI = request.getRequestURI();
        return statsClient.saveEndpointHit(app, requestURI, ipUser);
    }

    private long setViewsInEventDto(Event event) {
        long views;
        List<String> uri = List.of("/events/" + event.getId());
        List<ViewStatsDto> viewStats = statsClient.getViewStats(event.getCreatedOn().format(formatter),
                LocalDateTime.now().format(formatter), uri, true);
        if (viewStats.isEmpty()) {
            return 0;
        } else {
            views = viewStats.get(0).getHits();
        }
        return views;
    }

}
