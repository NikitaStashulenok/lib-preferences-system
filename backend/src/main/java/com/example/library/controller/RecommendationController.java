package com.example.library.controller;

import com.example.library.dto.BookDtos;
import com.example.library.service.RecommendationService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}/recommendations")
    public Page<BookDtos.RecommendationResponse> recommendations(@PathVariable Long userId,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size,
                                                                 @RequestParam(defaultValue = "all") String source) {
        return recommendationService.getRecommendations(userId, page, size, source);
    }
}
