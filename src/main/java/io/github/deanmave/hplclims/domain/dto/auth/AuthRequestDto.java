package io.github.deanmave.hplclims.domain.dto.auth;

import io.github.deanmave.hplclims.domain.dto.response.UserResponseDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public class AuthRequestDto {

    public record LoginRequest(
            @NotBlank(message = "Логин не должен быть пустым")
            String login,

            @NotBlank(message = "Пароль не должен быть пустым")
            String password
    ) {}

    @Builder
    public record AuthResponse(
            String token,
            UserResponseDto user
    ) {}
}