package ru.practicum.main.category.contollers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.services.CatPublicService;
import ru.practicum.main.category.dto.CategoryDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
public class CatPublicController {

    CatPublicService catPublicService;

    @GetMapping
    public List<CategoryDto> getAllCategories(@RequestParam(name = "from", defaultValue = "0") int from,
                                              @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GET - запрос на получение всех категорий");
        return catPublicService.getAllCategories(from, size);
    }

    @GetMapping("/catId}")
    public CategoryDto getCategoryById(@PathVariable long catId) {
        log.info("GET - запрос на получение категории с id={}", catId);
        return catPublicService.getCategoryByID(catId);
    }
}
