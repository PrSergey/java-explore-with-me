package ru.practicum.main.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.CompilationMapper;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.service.CompilationAdminService;
import ru.practicum.main.compilation.storage.CompilationRepository;
import ru.practicum.main.event.storage.EventRepository;
import ru.practicum.main.excepsion.ExistenceException;

import javax.persistence.EntityNotFoundException;


@Service
@RequiredArgsConstructor
public class CompilationAdminServiceImpl implements CompilationAdminService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto save(NewCompilationDto compilationDto) {
        Compilation compilation = new Compilation();
        if (compilationDto.getPinned() == null){
            compilation.setPinned(false);
        } else {
            compilation.setPinned(compilationDto.getPinned());
        }
        compilation.setTitle(compilationDto.getTitle());
        compilation.setEvents(eventRepository.findAllByIdIn(compilationDto.getEvents()));
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto update(long compId, NewCompilationDto compilationDto) {
        Compilation newCompilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найдена подборка событий с ID = %s", compId)));
        if (compilationDto.getTitle() != null) {
            newCompilation.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getPinned() != null) {
            newCompilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getEvents() != null) {
            newCompilation.setEvents(eventRepository.findAllByIdIn(compilationDto.getEvents()));
        }
        return compilationMapper.toCompilationDto(compilationRepository.save(newCompilation));
    }

    @Override
    public void delete(long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new ExistenceException("Compilation with id=" + compilationId + " was not found");
        }
        compilationRepository.deleteById(compilationId);
    }
}
