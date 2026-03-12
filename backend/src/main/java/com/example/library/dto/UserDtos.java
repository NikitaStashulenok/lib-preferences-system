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
            @Email String email,
            @Size(min = 2, max = 50, message = "Nickname must be 2..50 chars")
            @Pattern(regexp = "^[\\p{L}\\p{N} _.-]+$", message = "Nickname contains unsupported characters")
            String nickname,
            @Size(max = 1024, message = "Avatar URL is too long") String avatarUrl,
            String firstName,
            String lastName,
            LocalDate birthDate,
            String country,
            String city,
            String postalCode,
            String street,
            String houseNumber,
            @Pattern(regexp = "^\\+?[0-9()\\-\\s]{5,25}$", message = "Введите корректный номер телефона") String phoneNumber
    ) {}
}
