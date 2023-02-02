package io.github.dankoller.springrecipe.controller;

import io.github.dankoller.springrecipe.request.RegistrationRequest;
import io.github.dankoller.springrecipe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@SuppressWarnings("unused")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * This method handles the registration of a new user.
     *
     * @param request The request body containing the user's information
     * @return A response entity containing the status code
     */
    @PostMapping("/api/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) {
        return userService.register(request);
    }
}
