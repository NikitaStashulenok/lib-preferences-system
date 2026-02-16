package com.example.library.repository;

import com.example.library.model.RecommendationProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationProfileRepository extends JpaRepository<RecommendationProfile, Long> {
    Optional<RecommendationProfile> findByUserId(Long userId);
}
