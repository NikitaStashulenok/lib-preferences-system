package com.example.library.service;

import com.example.library.dto.AdminDtos;
import com.example.library.model.Book;
import com.example.library.model.Loan;
import com.example.library.model.LoanStatus;
import com.example.library.model.Reservation;
import com.example.library.model.ReservationStatus;
import com.example.library.model.User;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.ReservationRepository;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibrarianService {
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    public LibrarianService(LoanRepository loanRepository,
                            ReservationRepository reservationRepository,
                            ReservationService reservationService) {
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
        this.reservationService = reservationService;
    }

    @Transactional(readOnly = true)
    public Page<AdminDtos.LibrarianCirculationItemResponse> getCirculationItems(int page,
                                                                                int size,
                                                                                String userQuery,
                                                                                String bookQuery,
                                                                                String status) {
        List<AdminDtos.LibrarianCirculationItemResponse> items = new ArrayList<>();

        if (status == null || isLoanStatus(status)) {
            LoanStatus loanStatus = status == null ? null : LoanStatus.valueOf(status);
            loanRepository.searchAdmin(userQuery, bookQuery, loanStatus, PageRequest.of(0, Math.max(size * Math.max(page + 1, 1) * 2, 1000)))
                    .stream()
                    .map(this::toLoanItem)
                    .forEach(items::add);
        }

        if (status == null || isReservationStatus(status)) {
            ReservationStatus reservationStatus = status == null ? null : ReservationStatus.valueOf(status);
            reservationRepository.searchAdmin(userQuery, bookQuery, reservationStatus, PageRequest.of(0, Math.max(size * Math.max(page + 1, 1) * 2, 1000)))
                    .stream()
                    .filter(reservation -> reservation.getStatus() == ReservationStatus.NOTIFIED)
                    .map(this::toReservationItem)
                    .forEach(items::add);
        }

        items.sort(Comparator.comparing(this::sortDate).reversed().thenComparing(AdminDtos.LibrarianCirculationItemResponse::id, Comparator.reverseOrder()));

        int fromIndex = Math.min(page * size, items.size());
        int toIndex = Math.min(fromIndex + size, items.size());
        return new PageImpl<>(items.subList(fromIndex, toIndex), PageRequest.of(page, size), items.size());
    }

    @Transactional
    public AdminDtos.LibrarianCirculationItemResponse issueReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        if (reservation.getStatus() != ReservationStatus.NOTIFIED) {
            throw new IllegalStateException("Only notified reservations can be issued");
        }

        Book book = reservation.getBook();
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No copies available for issuing");
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);

        reservation.setStatus(ReservationStatus.FULFILLED);

        User user = reservation.getUser();
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setBorrowedAt(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setReturnedAt(null);
        return toLoanItem(loanRepository.save(loan));
    }

    @Transactional
    public AdminDtos.LibrarianCirculationItemResponse returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new IllegalStateException("Loan already closed");
        }
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnedAt(LocalDate.now());
        loan.getBook().setAvailableCopies(loan.getBook().getAvailableCopies() + 1);
        reservationService.notifyFirstWaitingForBook(loan.getBook().getId());
        return toLoanItem(loanRepository.save(loan));
    }

    private boolean isLoanStatus(String status) {
        return switch (status) {
            case "ACTIVE", "RETURNED", "OVERDUE" -> true;
            default -> false;
        };
    }

    private boolean isReservationStatus(String status) {
        return switch (status) {
            case "NOTIFIED", "WAITING", "CANCELLED", "EXPIRED", "FULFILLED" -> true;
            default -> false;
        };
    }

    private java.time.Instant sortDate(AdminDtos.LibrarianCirculationItemResponse item) {
        if (item.borrowedAt() != null) {
            return item.borrowedAt().atStartOfDay().toInstant(ZoneOffset.UTC);
        }
        if (item.notifiedAt() != null) {
            return item.notifiedAt();
        }
        return item.createdAt();
    }

    private AdminDtos.LibrarianCirculationItemResponse toLoanItem(Loan loan) {
        return new AdminDtos.LibrarianCirculationItemResponse(
                loan.getId(),
                "LOAN",
                loan.getUser().getId(),
                loan.getUser().getEmail(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getStatus().name(),
                loan.getBorrowedAt(),
                loan.getDueDate(),
                loan.getReturnedAt(),
                null,
                null,
                null,
                null
        );
    }

    private AdminDtos.LibrarianCirculationItemResponse toReservationItem(Reservation reservation) {
        return new AdminDtos.LibrarianCirculationItemResponse(
                reservation.getId(),
                "RESERVATION",
                reservation.getUser().getId(),
                reservation.getUser().getEmail(),
                reservation.getBook().getId(),
                reservation.getBook().getTitle(),
                reservation.getStatus().name(),
                null,
                null,
                null,
                reservation.getCreatedAt(),
                reservation.getNotifiedAt(),
                reservation.getExpiresAt(),
                reservation.getCancelledAt()
        );
    }
}
