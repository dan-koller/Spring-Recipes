package io.github.dankoller.springrecipe.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * This class represents a request to create a new recipe and is used for deserializing and validating the JSON
 * request body.
 */
@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class RecipeRequest {
    @NotBlank
    private String name;
    @NotBlank
    @NotEmpty
    private String category;
    @NotBlank
    private String description;
    @Size(min = 1)
    private String[] ingredients;
    @Size(min = 1)
    private String[] directions;

    // Constructor for Jackson to deserialize JSON to Java object
    public RecipeRequest(@JsonProperty(required = true, value = "name") String name,
                         @JsonProperty(required = true, value = "category") String category,
                         @JsonProperty(required = true, value = "description") String description,
                         @JsonProperty(required = true, value = "ingredients") String[] ingredients,
                         @JsonProperty(required = true, value = "directions") String[] directions) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.ingredients = ingredients;
        this.directions = directions;
    }
}
