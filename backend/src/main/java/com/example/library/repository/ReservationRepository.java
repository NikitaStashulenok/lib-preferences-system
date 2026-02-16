package com.example.library.repository;

import com.example.library.model.Reservation;
import com.example.library.model.ReservationStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Reservation> findFirstByBookIdAndStatusOrderByCreatedAtAsc(Long bookId, ReservationStatus status);
    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, ReservationStatus status);
}
