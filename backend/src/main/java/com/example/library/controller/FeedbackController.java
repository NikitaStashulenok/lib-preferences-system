package com.example.library.controller;

import com.example.library.dto.FeedbackDtos;
import com.example.library.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/books/{id}/ratings")
    public void createRating(@PathVariable Long id, @Valid @RequestBody FeedbackDtos.RatingRequest request) {
        feedbackService.createRating(id, request);
    }

    @PutMapping("/books/{id}/ratings/me")
    public void updateMyRating(@PathVariable Long id, @Valid @RequestBody FeedbackDtos.RatingRequest request) {
        feedbackService.updateMyRating(id, request);
    }

    @PostMapping("/books/{id}/reviews")
    public void createReview(@PathVariable Long id, @Valid @RequestBody FeedbackDtos.ReviewRequest request) {
        feedbackService.createReview(id, request);
    }

    @GetMapping("/books/{id}/reviews")
    public Page<FeedbackDtos.ReviewResponse> getReviews(@PathVariable Long id,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return feedbackService.getReviews(id, page, size)
                .map(review -> new FeedbackDtos.ReviewResponse(
                        review.getId(),
                        review.getBook().getId(),
                        review.getUser().getId(),
                        review.getText(),
                        review.getCreatedAt()));
    }

    @PostMapping("/users/{userId}/preferences")
    public void updatePreferences(@PathVariable Long userId, @RequestBody FeedbackDtos.PreferencesRequest request) {
        feedbackService.updatePreferences(userId, request);
    }
}
