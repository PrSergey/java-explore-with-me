package ru.practicum.main.category.contollers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.services.CatAdminService;
import ru.practicum.main.category.dto.CategoryDto;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/admin")
public class CatAdminController {

    CatAdminService catAdminService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto saveCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("POST - запрос на сохранение категории мероприятия в базу.");
        return catAdminService.saveCategory(categoryDto);
    }

    @PatchMapping("categories/{catId}")
    public CategoryDto updateCategory(@PathVariable long catId, @RequestBody @Valid CategoryDto categoryDto) {
        log.info("PATCH - запрос на обновление названия категории");
        return catAdminService.updateCategory(catId, categoryDto);
    }

    @DeleteMapping("categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long catId) {
        log.info("DELETE - запрос на удаление категории");
        catAdminService.deleteCategory(catId);
    }

}
