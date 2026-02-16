package com.example.library.repository;

import com.example.library.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByBookIdOrderByCreatedAtDesc(Long bookId, Pageable pageable);
}
