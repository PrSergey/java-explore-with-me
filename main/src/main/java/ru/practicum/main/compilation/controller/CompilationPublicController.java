package ru.practicum.main.compilation.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.service.CompilationPublicService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@Slf4j
@RequiredArgsConstructor
public class CompilationPublicController {

    private final CompilationPublicService compilationPublicService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        log.info("GET public - запрос public получения подборок событий по фильтру");
        return compilationPublicService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable long compId) {
        log.info("GET public - запрос public а получение подборки событий с id={}", compId);
        return compilationPublicService.getCompilationById(compId);
    }



}
