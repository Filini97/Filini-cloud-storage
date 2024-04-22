package ru.filini.cloudstorage.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.filini.cloudstorage.DTO.responses.AuthResponse;
import ru.filini.cloudstorage.DTO.requests.AuthRequest;
import ru.filini.cloudstorage.service.AuthService;
import ru.filini.cloudstorage.tables.User;

@RestController
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authenticationRequest) {
        return authService.login(authenticationRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String authToken) {
        authService.logout(authToken);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
