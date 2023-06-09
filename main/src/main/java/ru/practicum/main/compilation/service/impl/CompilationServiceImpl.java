package ru.practicum.main.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.CompilationMapper;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.service.CompilationService;
import ru.practicum.main.compilation.storage.CompilationRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.storage.EventRepository;
import ru.practicum.main.excepsion.ExistenceException;

import javax.persistence.EntityNotFoundException;
import javax.validation.UnexpectedTypeException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public CompilationDto save(NewCompilationDto compilationDto) {
        if (compilationDto.getTitle() == null || compilationDto.getTitle().isBlank()) {
            throw new UnexpectedTypeException("The title cannot be null and blank");
        }

        Compilation compilation = new Compilation();
        if (compilationDto.getPinned() == null) {
            compilation.setPinned(false);
        } else {
            compilation.setPinned(compilationDto.getPinned());
        }
        compilation.setTitle(compilationDto.getTitle());
        compilation.setEvents(getEvents(compilationDto.getEvents()));
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public CompilationDto update(long compId, NewCompilationDto compilationDto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найдена подборка событий с ID = %s", compId)));
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(compilationDto.getEvents()));
        }
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void delete(long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new ExistenceException("Compilation with id=" + compilationId + " was not found");
        }

        compilationRepository.deleteById(compilationId);
    }

    private List<Event> getEvents(List<Long> eventsIds) {
        List<Event> events = new ArrayList<>();
        if (eventsIds == null) {
            return events;
        } else {
            events.addAll(eventRepository.findAllByIdIn(eventsIds));
            return events;
        }
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return compilationRepository.findAllByPinned(pinned, pageRequest).stream()
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(long comId) {
        Compilation compilation = compilationRepository.findById(comId)
                .orElseThrow(() -> new ExistenceException("Compilation with id=" + comId + " was not found"));
        return compilationMapper.toCompilationDto(compilation);
    }

}
