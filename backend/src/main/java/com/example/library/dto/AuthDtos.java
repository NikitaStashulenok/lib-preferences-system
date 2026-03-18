package com.example.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {
    public record RegisterRequest(
            @Email(message = "Введите корректный e-mail") String email,
            @NotBlank(message = "Введите пароль")
            @Size(min = 6, max = 255, message = "Пароль должен содержать минимум 6 символов") String password
    ) {}

    public record LoginRequest(
            @Email(message = "Введите корректный e-mail") String email,
            @NotBlank(message = "Введите пароль") String password
    ) {}

    public record RefreshRequest(@NotBlank(message = "Refresh token обязателен") String refreshToken) {}
    public record LogoutRequest(@NotBlank(message = "Refresh token обязателен") String refreshToken) {}
    public record AuthResponse(String accessToken, String refreshToken) {}
}
