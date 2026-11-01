package io.github.deanmave.hplclims.controller;

import io.github.deanmave.hplclims.domain.dto.auth.AuthRequestDto;
import io.github.deanmave.hplclims.service.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthRequestDto.AuthResponse> login(@Valid @RequestBody AuthRequestDto.LoginRequest request) {
        AuthRequestDto.AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}