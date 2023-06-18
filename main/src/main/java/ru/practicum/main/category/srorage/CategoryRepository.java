package ru.practicum.main.category.srorage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.category.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByName(String catName);

    boolean existsAllByName(String name);

}
