package io.github.dankoller.springrecipe.persistence;

import io.github.dankoller.springrecipe.entity.Recipe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This interface is used to interact with the recipe table in the database.
 */
@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Long> {
    List<Recipe> findAllByNameContainingIgnoreCaseOrderByDateDesc(String name);

    List<Recipe> findAllByCategoryIgnoreCaseOrderByDateDesc(String category);
}
