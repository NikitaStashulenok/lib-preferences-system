package com.example.library.service;

import com.example.library.dto.ReservationDtos;
import com.example.library.model.Book;
import com.example.library.model.Reservation;
import com.example.library.model.ReservationStatus;
import com.example.library.model.User;
import com.example.library.repository.BookRepository;
import com.example.library.repository.ReservationRepository;
import com.example.library.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CurrentUserService currentUserService;

    public ReservationService(ReservationRepository reservationRepository,
                              UserRepository userRepository,
                              BookRepository bookRepository,
                              CurrentUserService currentUserService) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public ReservationDtos.ReservationResponse create(ReservationDtos.CreateReservationRequest request) {
        currentUserService.requireSameUserOrAdmin(request.userId());

        User user = userRepository.findById(request.userId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Book book = bookRepository.findById(request.bookId()).orElseThrow(() -> new IllegalArgumentException("Book not found"));
        if (book.getAvailableCopies() > 0) {
            throw new IllegalStateException("Reservation is available only when all copies are unavailable");
        }
        boolean exists = reservationRepository.existsByUserIdAndBookIdAndStatus(user.getId(), book.getId(), ReservationStatus.WAITING);
        if (exists) {
            throw new IllegalArgumentException("Active reservation already exists for this user/book");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.WAITING);
        reservation.setCreatedAt(Instant.now());
        return toDto(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationDtos.ReservationResponse cancel(Long reservationId, Long userId) {
        currentUserService.requireSameUserOrAdmin(userId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        if (!reservation.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Reservation does not belong to user");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED || reservation.getStatus() == ReservationStatus.FULFILLED) {
            throw new IllegalStateException("Reservation already closed");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(Instant.now());
        return toDto(reservationRepository.save(reservation));
    }

    @Transactional(readOnly = true)
    public List<ReservationDtos.ReservationResponse> getUserReservations(Long userId) {
        currentUserService.requireSameUserOrAdmin(userId);
        return reservationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDto).toList();
    }

    @Transactional
    public Reservation notifyFirstWaitingForBook(Long bookId) {
        return reservationRepository.findFirstByBookIdAndStatusOrderByCreatedAtAsc(bookId, ReservationStatus.WAITING)
                .map(reservation -> {
                    reservation.setStatus(ReservationStatus.NOTIFIED);
                    reservation.setNotifiedAt(Instant.now());
                    reservation.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
                    return reservationRepository.save(reservation);
                })
                .orElse(null);
    }

    private ReservationDtos.ReservationResponse toDto(Reservation reservation) {
        return new ReservationDtos.ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getBook().getId(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getNotifiedAt(),
                reservation.getExpiresAt(),
                reservation.getCancelledAt());
    }
}
