package com.example.library.repository;

import com.example.library.model.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserIdAndBookId(Long userId, Long bookId);
    List<Rating> findByUserIdAndScoreGreaterThanEqual(Long userId, Integer score);

    @Query("select avg(r.score) from Rating r where r.book.id = :bookId")
    Double findAverageScoreByBookId(@Param("bookId") Long bookId);

    long countByBookId(Long bookId);

    @Query("""
            select r from Rating r
            where (:userQuery is null or :userQuery = ''
                   or lower(r.user.email) like lower(concat('%', :userQuery, '%')))
              and (:bookQuery is null or :bookQuery = ''
                   or lower(r.book.title) like lower(concat('%', :bookQuery, '%')))
              and (:score is null or r.score = :score)
            """)
    Page<Rating> searchAdmin(@Param("userQuery") String userQuery,
                             @Param("bookQuery") String bookQuery,
                             @Param("score") Integer score,
                             Pageable pageable);
}
