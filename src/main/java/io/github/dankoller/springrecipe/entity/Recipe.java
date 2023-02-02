package io.github.dankoller.springrecipe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.dankoller.springrecipe.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * This class represents a recipe. It is also used as response object for the REST API.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    @JsonIgnore
    private long id;
    @Column
    private String name;
    @Column
    private String category;
    @Column
    private LocalDateTime date;
    @Column
    private String description;
    @Column
    private String[] ingredients;
    @Column
    private String[] directions;
    @JsonIgnore
    @ManyToOne
    private User author;

    public Recipe(String name, String category, LocalDateTime date, String description, String[] ingredients, String[] directions, User author) {
        this.name = name;
        this.category = category;
        this.date = date;
        this.description = description;
        this.ingredients = ingredients;
        this.directions = directions;
        this.author = author;
    }
}
