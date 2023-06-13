package ru.practicum.main.compilation.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.service.CompilationAdminService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {

    private final CompilationAdminService compilationAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto save(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("POST - запрос добавление новой подборки");
        return compilationAdminService.save(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@PathVariable long compId,
                                 @RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("PATCH - запрос обновить информацию о подборке");
        return compilationAdminService.update(compId, newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public HttpStatus delete(@PathVariable long compId) {
        log.info("DELETE - запрос на удаление подборки");
        compilationAdminService.delete(compId);
        return HttpStatus.NO_CONTENT;
    }

}
