package io.github.dankoller.springrecipe.service;

import io.github.dankoller.springrecipe.entity.Recipe;
import io.github.dankoller.springrecipe.persistence.RecipeRepository;
import io.github.dankoller.springrecipe.persistence.UserRepository;
import io.github.dankoller.springrecipe.request.RecipeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@SuppressWarnings("unused")
public class RecipeService {
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * This method is used to create a new recipe from a RecipeRequest object.
     *
     * @param username      The username of the user creating the recipe (used to set the author)
     * @param recipeRequest The RecipeRequest object containing the recipe information
     * @return A ResponseEntity containing the ID of the newly created recipe
     */
    public ResponseEntity<?> postRecipe(String username, RecipeRequest recipeRequest) {
        if (isValidRecipeRequest(recipeRequest)) {
            Recipe recipe = new Recipe(
                    recipeRequest.getName(),
                    recipeRequest.getCategory(),
                    LocalDateTime.now(),
                    recipeRequest.getDescription(),
                    recipeRequest.getIngredients(),
                    recipeRequest.getDirections(),
                    userRepository.findByEmailIgnoreCase(username));
            recipeRepository.save(recipe);
            return new ResponseEntity<>(Map.of("id", recipe.getId()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method is used to get a recipe by its ID.
     *
     * @param id The ID of the recipe to get
     * @return A ResponseEntity containing the recipe if it exists, or 404 if it doesn't
     */
    public ResponseEntity<Recipe> getRecipe(long id) {
        if (!recipeRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<Recipe> recipe = recipeRepository.findById(id);
        return recipe.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * This method is used to delete a recipe by its ID.
     *
     * @param username The username of the user deleting the recipe
     * @param id       The ID of the recipe to delete
     * @return A ResponseEntity containing 204 if the recipe was deleted, 403 if the user is not the owner,
     * or 404 if the recipe doesn't exist
     */
    public ResponseEntity<?> deleteRecipe(String username, long id) {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        // If the recipe doesn't exist, return 404
        if (recipe == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // If the recipe exists, but the user is not the owner, return 403
        if (!recipe.getAuthor().getEmail().equals(username)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Delete the recipe
        recipeRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This method is used to update a recipe by its ID.
     *
     * @param username      The username of the user updating the recipe
     * @param id            The ID of the recipe to update
     * @param recipeRequest The RecipeRequest object containing the updated recipe information
     * @return A ResponseEntity containing 204 if the recipe was updated, 403 if the user is not the owner,
     * 404 if the recipe doesn't exist, or 400 if the recipe is invalid
     */
    public ResponseEntity<?> updateRecipe(String username, long id, RecipeRequest recipeRequest) {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        // If the recipe doesn't exist, return 404
        if (recipe == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // If the recipe exists, but the user is not the owner, return 403
        if (!recipe.getAuthor().getEmail().equals(username)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Validate the recipe request
        if (isValidRecipeRequest(recipeRequest)) {
            // Update the recipe
            recipe.setName(recipeRequest.getName());
            recipe.setCategory(recipeRequest.getCategory());
            recipe.setDescription(recipeRequest.getDescription());
            recipe.setIngredients(recipeRequest.getIngredients());
            recipe.setDirections(recipeRequest.getDirections());
            recipeRepository.save(recipe);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method is used to query recipes by category or name.
     *
     * @param category The category to query by
     * @param name     The name to query by
     * @return A ResponseEntity containing a list of recipes if the query was successful,
     * or 400 if the query was invalid
     */
    public ResponseEntity<?> getRecipeByParam(String category, String name) {
        // Only one parameter is allowed
        if (category != null && name != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (category != null) {
            return new ResponseEntity<>(recipeRepository.findAllByCategoryIgnoreCaseOrderByDateDesc(category), HttpStatus.OK);
        } else if (name != null) {
            return new ResponseEntity<>(recipeRepository.findAllByNameContainingIgnoreCaseOrderByDateDesc(name), HttpStatus.OK);
        } else {
            // No parameters specified (return 400 for now)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * A helper method to validate a RecipeRequest object.
     *
     * @param recipeRequest The RecipeRequest object to validate
     * @return True if the RecipeRequest object is valid, false otherwise
     */
    private boolean isValidRecipeRequest(RecipeRequest recipeRequest) {
        boolean isValidName = recipeRequest.getName() != null
                && !recipeRequest.getName().isEmpty()
                && !recipeRequest.getName().isBlank();
        boolean isValidCategory = recipeRequest.getCategory() != null
                && !recipeRequest.getCategory().isEmpty()
                && !recipeRequest.getCategory().isBlank();
        boolean isValidDescription = recipeRequest.getDescription() != null
                && !recipeRequest.getDescription().isEmpty()
                && !recipeRequest.getDescription().isBlank();
        boolean isValidIngredients = recipeRequest.getIngredients() != null
                && recipeRequest.getIngredients().length > 0;
        boolean isValidDirections = recipeRequest.getDirections() != null
                && recipeRequest.getDirections().length > 0;
        return isValidName && isValidCategory && isValidDescription && isValidIngredients && isValidDirections;
    }
}
