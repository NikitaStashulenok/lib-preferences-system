package com.example.library.controller;

import com.example.library.dto.UserDtos;
import com.example.library.model.Role;
import com.example.library.model.User;
import com.example.library.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public Page<User> users(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    @PatchMapping("/users/{id}")
    public User patchUser(@PathVariable Long id, @Valid @RequestBody UserDtos.AdminPatchUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.email() != null && !request.email().isBlank()) {
            String normalizedEmail = request.email().trim();
            if (!normalizedEmail.equalsIgnoreCase(user.getEmail())) {
                userRepository.findByEmail(normalizedEmail).ifPresent(existing -> {
                    if (!existing.getId().equals(user.getId())) {
                        throw new IllegalStateException("Email already in use");
                    }
                });
            }
            user.setEmail(normalizedEmail);
        }

        if (request.fullName() != null) {
            user.setFullName(normalize(request.fullName()));
        }
        if (request.firstName() != null) {
            user.setFirstName(normalize(request.firstName()));
        }
        if (request.lastName() != null) {
            user.setLastName(normalize(request.lastName()));
        }
        if (request.birthDate() != null) {
            user.setBirthDate(request.birthDate());
        }
        if (request.country() != null) {
            user.setCountry(normalize(request.country()));
        }
        if (request.city() != null) {
            user.setCity(normalize(request.city()));
        }
        if (request.postalCode() != null) {
            user.setPostalCode(normalize(request.postalCode()));
        }
        if (request.street() != null) {
            user.setStreet(normalize(request.street()));
        }
        if (request.houseNumber() != null) {
            user.setHouseNumber(normalize(request.houseNumber()));
        }
        if (request.phoneNumber() != null) {
            user.setPhoneNumber(normalize(request.phoneNumber()));
        }

        return userRepository.save(user);
    }

    @PatchMapping("/users/{id}/roles")
    public User patchRoles(@PathVariable Long id, @RequestBody Set<Role> roles) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRoles(roles);
        return userRepository.save(user);
    }

    private String normalize(String value) {
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
