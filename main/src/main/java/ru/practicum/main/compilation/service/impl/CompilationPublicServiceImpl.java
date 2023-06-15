package ru.practicum.main.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.CompilationMapper;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.service.CompilationPublicService;
import ru.practicum.main.compilation.storage.CompilationRepository;
import ru.practicum.main.excepsion.ExistenceException;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationPublicServiceImpl implements CompilationPublicService {

    private final CompilationRepository compilationRepository;

    private final CompilationMapper compilationMapper;

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
