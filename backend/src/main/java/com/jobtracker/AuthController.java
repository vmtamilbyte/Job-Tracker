package com.jobtracker;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    record Creds(@Email @NotBlank String email, @NotBlank @Size(min = 6) String password) {}
    record TokenRes(String token) {}

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users; this.encoder = encoder; this.jwt = jwt;
    }

    @PostMapping("/register")
    TokenRes register(@Valid @RequestBody Creds c) {
        String email = c.email().toLowerCase();
        if (users.findByEmail(email).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(c.password()));
        return new TokenRes(jwt.create(users.save(u).getId()));
    }

    @PostMapping("/login")
    TokenRes login(@Valid @RequestBody Creds c) {
        User u = users.findByEmail(c.email().toLowerCase())
                .filter(x -> encoder.matches(c.password(), x.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return new TokenRes(jwt.create(u.getId()));
    }
}
