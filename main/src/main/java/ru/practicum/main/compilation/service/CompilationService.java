package ru.practicum.main.compilation.service;

import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto save(NewCompilationDto compilationDto);

    CompilationDto update(long compId, NewCompilationDto compilationDto);

    void delete(long compilationId);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(long comId);

}
