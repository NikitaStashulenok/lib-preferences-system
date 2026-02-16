package com.example.library.dto;

import com.example.library.model.LoanStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class LoanDtos {
    public record BorrowRequest(@NotNull Long bookId, @NotNull Long userId) {}
    public record LoanResponse(Long id, Long userId, Long bookId, LoanStatus status,
                               LocalDate borrowedAt, LocalDate dueDate, LocalDate returnedAt) {}
}
