package com.example.library.controller;

import com.example.library.dto.AdminDtos;
import com.example.library.service.LibrarianService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/librarian")
public class LibrarianController {
    private final LibrarianService librarianService;

    public LibrarianController(LibrarianService librarianService) {
        this.librarianService = librarianService;
    }

    @GetMapping("/reservations")
    public Page<AdminDtos.AdminReservationResponse> reservations(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size,
                                                                 @RequestParam(required = false) String userQuery,
                                                                 @RequestParam(required = false) String bookQuery,
                                                                 @RequestParam(required = false) String status) {
        return librarianService.reservations(page, size, userQuery, bookQuery, status);
    }

    @PostMapping("/reservations/{id}/issue")
    public AdminDtos.AdminReservationResponse issue(@PathVariable Long id) {
        return librarianService.issueReservation(id);
    }

    @PostMapping("/reservations/{id}/return")
    public AdminDtos.AdminReservationResponse markReturned(@PathVariable Long id) {
        return librarianService.returnReservation(id);
    }
}
