package com.cloudproject.TeamC.CampEat.infrastructure.repository;

import com.cloudproject.TeamC.CampEat.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByPlaceIdAndIsHiddenFalse(Long placeId, Pageable pageable);

    @Query("SELECT r FROM Review r " +
            "LEFT JOIN ReviewLike rl ON rl.review = r " +
            "WHERE r.place.id = :placeId AND r.isHidden = false " +
            "GROUP BY r " +
            "ORDER BY COUNT(rl) DESC, r.createdAt DESC")
    Page<Review> findAllByPlaceIdOrderByLikesDesc(@Param("placeId") Long placeId, Pageable pageable);

    @Query("SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review.id = :reviewId")
    long countLikesByReviewId(@Param("reviewId") Long reviewId);
}
