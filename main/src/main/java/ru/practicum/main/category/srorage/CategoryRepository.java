package ru.practicum.main.category.srorage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
