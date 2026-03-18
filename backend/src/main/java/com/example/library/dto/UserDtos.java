package com.example.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

public class UserDtos {
    public record UserProfileResponse(
            Long id,
            String email,
            String nickname,
            String avatarUrl,
            String firstName,
            String lastName,
            LocalDate birthDate,
            String country,
            String city,
            String postalCode,
            String street,
            String houseNumber,
            String phoneNumber,
            Set<String> roles
    ) {}

    public record UpdateProfileRequest(
            @Email(message = "Введите корректный e-mail") String email,
            @Size(min = 2, max = 50, message = "Никнейм должен содержать от 2 до 50 символов")
            @Pattern(regexp = "^[\\p{L}\\p{N} _.-]+$", message = "Никнейм содержит недопустимые символы")
            String nickname,
            @Size(max = 1024, message = "Ссылка на аватар слишком длинная") String avatarUrl,
            @Size(max = 100, message = "Имя не должно превышать 100 символов") String firstName,
            @Size(max = 100, message = "Фамилия не должна превышать 100 символов") String lastName,
            LocalDate birthDate,
            @Size(max = 100, message = "Название страны не должно превышать 100 символов") String country,
            @Size(max = 100, message = "Название города не должно превышать 100 символов") String city,
            @Pattern(regexp = "^[A-Za-z0-9\\- ]{3,12}$", message = "Введите корректный почтовый индекс") String postalCode,
            @Size(max = 120, message = "Название улицы не должно превышать 120 символов") String street,
            @Size(max = 20, message = "Номер дома не должен превышать 20 символов") String houseNumber,
            @Pattern(regexp = "^\\+?[0-9()\\-\\s]{7,25}$", message = "Введите корректный номер телефона") String phoneNumber
    ) {}
}
