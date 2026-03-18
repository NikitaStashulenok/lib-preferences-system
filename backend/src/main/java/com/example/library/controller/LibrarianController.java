package com.example.library.controller;

import com.example.library.dto.AdminDtos;
import com.example.library.service.LibrarianService;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/librarian")
public class LibrarianController {
    private final LibrarianService librarianService;

    public LibrarianController(LibrarianService librarianService) {
        this.librarianService = librarianService;
    }

    @GetMapping("/loans")
    public Page<AdminDtos.LibrarianCirculationItemResponse> loans(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "20") int size,
                                                                  @RequestParam(required = false) String userQuery,
                                                                  @RequestParam(required = false) String bookQuery,
                                                                  @RequestParam(required = false) String status) {
        return librarianService.getCirculationItems(page, size, userQuery, bookQuery, parseStatus(status));
    }

    @PostMapping("/reservations/{id}/issue")
    public AdminDtos.LibrarianCirculationItemResponse issueReservation(@PathVariable Long id) {
        return librarianService.issueReservation(id);
    }

    @PostMapping("/loans/{id}/return")
    public AdminDtos.LibrarianCirculationItemResponse returnLoan(@PathVariable Long id) {
        return librarianService.returnLoan(id);
    }

    private String parseStatus(String value) {
        return value == null || value.isBlank() ? null : value.trim().toUpperCase(Locale.ROOT);
    }
}
