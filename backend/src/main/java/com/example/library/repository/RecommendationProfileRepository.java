package com.example.library.repository;

import com.example.library.model.RecommendationProfile;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecommendationProfileRepository extends JpaRepository<RecommendationProfile, Long> {
    Optional<RecommendationProfile> findByUserId(Long userId);

    @Query("""
            select rp from RecommendationProfile rp
            where (:userQuery is null or :userQuery = ''
                   or lower(rp.user.email) like lower(concat('%', :userQuery, '%')))
            """)
    Page<RecommendationProfile> searchAdmin(@Param("userQuery") String userQuery, Pageable pageable);
}
