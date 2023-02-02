package io.github.dankoller.springrecipe.controller;

import io.github.dankoller.springrecipe.entity.Recipe;
import io.github.dankoller.springrecipe.entity.user.UserDetailsImpl;
import io.github.dankoller.springrecipe.request.RecipeRequest;
import io.github.dankoller.springrecipe.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@SuppressWarnings("unused")
public class RecipeController {
    @Autowired
    private RecipeService recipeService;

    /**
     * This endpoint is used to create a new recipe.
     *
     * @param user          The user that is creating the recipe
     * @param recipeRequest The recipe to be created
     * @return A response entity with the recipe that was created
     */
    @PostMapping("/api/recipe/new")
    public ResponseEntity<?> postRecipe(@AuthenticationPrincipal UserDetailsImpl user,
                                        @RequestBody RecipeRequest recipeRequest) {
        return recipeService.postRecipe(user.getUsername(), recipeRequest);
    }

    /**
     * This endpoint is used to get a recipe by its id.
     *
     * @param id The id of the recipe to be retrieved
     * @return A response entity with the recipe that was retrieved
     */
    @GetMapping("/api/recipe/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable int id) {
        return recipeService.getRecipe(id);
    }

    /**
     * This endpoint is used to delete a specific recipe.
     *
     * @param user The user that is deleting the recipe
     * @param id   The id of the recipe to be deleted
     * @return A response entity with the status of the deletion
     */
    @DeleteMapping("/api/recipe/{id}")
    public ResponseEntity<?> deleteRecipe(@AuthenticationPrincipal UserDetailsImpl user,
                                          @PathVariable int id) {
        return recipeService.deleteRecipe(user.getUsername(), id);
    }

    /**
     * This endpoint is used to update a specific recipe.
     *
     * @param user          The user that is updating the recipe
     * @param id            The id of the recipe to be updated
     * @param recipeRequest The recipe to be updated
     * @return A response entity with the status of the update
     */
    @PutMapping("/api/recipe/{id}")
    public ResponseEntity<?> updateRecipe(@AuthenticationPrincipal UserDetailsImpl user,
                                          @PathVariable int id,
                                          @RequestBody RecipeRequest recipeRequest) {
        return recipeService.updateRecipe(user.getUsername(), id, recipeRequest);
    }

    /**
     * This endpoint is used to get all recipes queried by category or name.
     *
     * @param category The category of the recipe to be retrieved
     * @param name     The name of the recipe to be retrieved
     * @return A response entity with the recipe that was retrieved
     */
    @GetMapping("/api/recipe/search")
    public ResponseEntity<?> getRecipeByParam(@RequestParam(required = false) String category,
                                              @RequestParam(required = false) String name) {
        return recipeService.getRecipeByParam(category, name);
    }
}
