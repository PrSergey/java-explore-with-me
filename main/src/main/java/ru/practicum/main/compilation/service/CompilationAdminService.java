package ru.practicum.main.compilation.service;

import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;

public interface CompilationAdminService {

    CompilationDto save(NewCompilationDto compilationDto);

    CompilationDto update(long compId, NewCompilationDto compilationDto);

    void delete(long compilationId);

}
