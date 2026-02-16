package com.example.library.service;

import com.example.library.dto.FeedbackDtos;
import com.example.library.model.*;
import com.example.library.repository.*;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackService {
    private final RatingRepository ratingRepository;
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final RecommendationProfileRepository profileRepository;

    public FeedbackService(RatingRepository ratingRepository, ReviewRepository reviewRepository, BookRepository bookRepository,
                           UserRepository userRepository, RecommendationProfileRepository profileRepository) {
        this.ratingRepository = ratingRepository;
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public void createRating(Long bookId, FeedbackDtos.RatingRequest request) {
        User user = userRepository.findById(request.userId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Book not found"));
        Rating rating = ratingRepository.findByUserIdAndBookId(request.userId(), bookId).orElseGet(Rating::new);
        rating.setUser(user);
        rating.setBook(book);
        rating.setScore(request.score());
        ratingRepository.save(rating);
    }

    @Transactional
    public void updateMyRating(Long bookId, FeedbackDtos.RatingRequest request) {
        createRating(bookId, request);
    }

    @Transactional
    public void createReview(Long bookId, FeedbackDtos.ReviewRequest request) {
        User user = userRepository.findById(request.userId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Book not found"));
        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setText(request.text());
        review.setCreatedAt(Instant.now());
        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public Page<Review> getReviews(Long bookId, int page, int size) {
        return reviewRepository.findByBookIdOrderByCreatedAtDesc(bookId, PageRequest.of(page, size));
    }

    @Transactional
    public void updatePreferences(Long userId, FeedbackDtos.PreferencesRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        RecommendationProfile profile = profileRepository.findByUserId(userId).orElseGet(RecommendationProfile::new);
        profile.setUser(user);
        profile.setPreferredGenresCsv(request.preferredGenresCsv());
        profileRepository.save(profile);
    }
}
