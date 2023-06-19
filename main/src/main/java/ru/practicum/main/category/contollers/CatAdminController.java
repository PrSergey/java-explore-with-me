package ru.practicum.main.category.contollers;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.services.CatService;
import ru.practicum.main.category.dto.CategoryDto;

import javax.validation.Valid;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/admin")
public class CatAdminController {

    CatService catService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto saveCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("POST admin - запрос на сохранение категории мероприятия в базу.");
        return catService.saveCategory(categoryDto);
    }

    @PatchMapping("categories/{catId}")
    public CategoryDto updateCategory(@PathVariable long catId, @RequestBody @Valid CategoryDto categoryDto) {
        log.info("PATCH admin - запрос на обновление названия категории");
        return catService.updateCategory(catId, categoryDto);
    }

    @DeleteMapping("categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public HttpStatus deleteCategory(@PathVariable long catId) {
        log.info("DELETE admin - запрос на удаление категории");
        catService.deleteCategory(catId);
        return HttpStatus.NO_CONTENT;
    }

}
