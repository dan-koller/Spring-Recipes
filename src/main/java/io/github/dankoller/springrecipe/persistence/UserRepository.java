package io.github.dankoller.springrecipe.persistence;

import io.github.dankoller.springrecipe.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This interface is used to interact with the user table in the database.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailIgnoreCase(String email);

    List<User> findAll();
}
