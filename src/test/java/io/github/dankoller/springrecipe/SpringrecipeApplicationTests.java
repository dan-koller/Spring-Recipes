package io.github.dankoller.springrecipe;

import io.github.dankoller.springrecipe.entity.Recipe;
import io.github.dankoller.springrecipe.entity.user.User;
import io.github.dankoller.springrecipe.persistence.RecipeRepository;
import io.github.dankoller.springrecipe.persistence.UserRepository;
import io.github.dankoller.springrecipe.service.RecipeService;
import io.github.dankoller.springrecipe.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings({"unused", "FieldCanBeLocal"})
class SpringrecipeApplicationTests {
    // Users
    private static final String randomUserId = UUID.randomUUID().toString().substring(0, 10).replaceAll("-", "");
    private final String validUserEmail = randomUserId + "@gmail.com";
    private static final String validUserPassword = UUID.randomUUID().toString().substring(0, 10);
    private final String invalidUserEmail = "test.@.com";
    private final String invalidUserPassword = "test";

    // Recipes
    private final String validRecipeJson = """
            {
              "name": "Test Recipe",
              "category": "Test Category",
              "description": "Test Description",
              "ingredients": ["boiled water", "honey", "fresh mint leaves"],
              "directions": ["Boil water", "Pour boiling hot water into a mug", "Add fresh mint leaves",\s
              "Mix and let the mint leaves seep for 3-5 minutes", "Add honey and mix again"]
            }""";
    private final String invalidRecipeJson = """
            {
              "name": "      ",
              "category": "      ",
              "description": "       ",
              "ingredients": ["Something"],
              "directions": ["Do stuff"]
            }""";
    private final String patchedRecipeJson = """
            {
              "name": "Patched Recipe",
              "category": "Patched Category",
              "description": "Test Description",
              "ingredients": ["boiled water", "honey", "fresh mint leaves"],
              "directions": ["Boil water", "Pour boiling hot water into a mug", "Add fresh mint leaves",\s
              "Mix and let the mint leaves seep for 3-5 minutes", "Add honey and mix again"]
            }""";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private RecipeService recipeService;

    // Test if the controllers are initialized
    @Test
    @Order(1)
    void contextLoads() {
        assertThat(userRepository).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(recipeRepository).isNotNull();
        assertThat(recipeService).isNotNull();
    }

    // Test if the user registration works
    @Test
    @Order(2)
    void testUserRegistration() throws Exception {
        // Test valid user registration
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"email\": \"" + validUserEmail + "\",\n" +
                                "  \"password\": \"" + validUserPassword + "\"\n" +
                                "}"))
                .andExpect(status().isOk());

        // Test invalid user registration
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"email\": \"" + invalidUserEmail + "\",\n" +
                                "  \"password\": \"" + invalidUserPassword + "\"\n" +
                                "}"))
                .andExpect(status().isBadRequest());
    }

    // Test if the user can post a new recipe
    @Test
    @Order(3)
    void testRecipeCreation() throws Exception {
        // Set user as authenticated
        setUserAsAuthenticated();

        // Test valid recipe creation
        mockMvc.perform(post("/api/recipe/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRecipeJson))
                .andExpect(status().isOk());

        // Test invalid recipe creation
        mockMvc.perform(post("/api/recipe/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRecipeJson))
                .andExpect(status().isBadRequest());
    }

    // Test if the user can patch a recipe
    @Test
    @Order(4)
    void testRecipePatching() throws Exception {
        // Set user as authenticated
        setUserAsAuthenticated();

        // Test valid recipe patching
        mockMvc.perform(put("/api/recipe/" + getLatestRecipeId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchedRecipeJson))
                .andExpect(status().isNoContent());

        // Test invalid recipe patching
        mockMvc.perform(put("/api/recipe/" + getLatestRecipeId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRecipeJson))
                .andExpect(status().isBadRequest());
    }

    // Test if the user can query a recipe by name
    @Test
    @Order(5)
    void testRecipeQuery() throws Exception {
        // Set user as authenticated
        setUserAsAuthenticated();

        // Test valid recipe query
        mockMvc.perform(get("/api/recipe/search?name=Patched Recipe"))
                .andExpect(status().isOk());

        // Test invalid recipe query
        mockMvc.perform(get("/api/recipe/search?name=Invalid Recipe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // Test if the user can query a recipe by category
    @Test
    @Order(6)
    void testRecipeQueryByCategory() throws Exception {
        // Set user as authenticated
        setUserAsAuthenticated();

        // Test valid recipe query
        mockMvc.perform(get("/api/recipe/search?category=Test Category"))
                .andExpect(status().isOk());

        // Test invalid recipe query
        mockMvc.perform(get("/api/recipe/search?category=Invalid Category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // Test if the user can delete a recipe
    @Test
    @Order(7)
    void testRecipeDeletion() throws Exception {
        // Set user as authenticated
        setUserAsAuthenticated();

        // Test valid recipe deletion
        mockMvc.perform(delete("/api/recipe/" + getLatestRecipeId()))
                .andExpect(status().isNoContent());

        // Test invalid recipe deletion
        mockMvc.perform(delete("/api/recipe/" + getLatestRecipeId()))
                .andExpect(status().isNotFound());
    }

    // Cleanups (Test if the user is deleted)
    @Test
    @Order(8)
    void cleanup() {
        User user = userRepository.findByEmailIgnoreCase(validUserEmail);
        userRepository.delete(user);
        assertThat(userRepository.findByEmailIgnoreCase(validUserEmail)).isNull();
    }

    /**
     * Helper method to set the user as authenticated.
     */
    private void setUserAsAuthenticated() {
        // Add AuthenticationPrincipal to the SecurityContext
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(validUserEmail, validUserPassword));
    }

    /**
     * Helper method to get the latest recipe id.
     */
    private long getLatestRecipeId() {
        Iterable<Recipe> recipes = recipeRepository.findAll();
        long latestRecipeId = 0L;
        for (Recipe recipe : recipes) {
            if (recipe.getId() > latestRecipeId) {
                latestRecipeId = recipe.getId();
            }
        }
        return latestRecipeId;
    }
}
