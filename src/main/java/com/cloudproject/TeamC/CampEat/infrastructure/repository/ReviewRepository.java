package com.cloudproject.TeamC.CampEat.infrastructure.repository;

import com.cloudproject.TeamC.CampEat.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r, COUNT(rl) FROM Review r " +
            "LEFT JOIN ReviewLike rl ON rl.review = r " +
            "WHERE r.place.id = :placeId AND r.isHidden = false " +
            "GROUP BY r")
    Page<Object[]> findReviewsWithLikeCount(@Param("placeId") Long placeId, Pageable pageable);

    @Query("SELECT r, COUNT(rl) FROM Review r " +
            "LEFT JOIN ReviewLike rl ON rl.review = r " +
            "WHERE r.place.id = :placeId AND r.isHidden = false " +
            "GROUP BY r " +
            "ORDER BY COUNT(rl) DESC, r.createdAt DESC")
    Page<Object[]> findReviewsWithLikeCountOrderByLikesDesc(@Param("placeId") Long placeId, Pageable pageable);

    @Query("SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review.id = :reviewId")
    long countLikesByReviewId(@Param("reviewId") Long reviewId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.place.id = :placeId AND r.isHidden = false")
    Double findAverageRatingByPlaceId(@Param("placeId") Long placeId);

    @Query("SELECT ri.imageUrl FROM Review r " +
            "JOIN r.images ri " +
            "WHERE r.place.id = :placeId AND r.isHidden = false " +
            "ORDER BY r.createdAt DESC")
    List<String> findLatestReviewImageByPlaceId(@Param("placeId") Long placeId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.place.id = :placeId AND r.isHidden = false")
    Long countByPlaceIdAndIsHiddenFalse(@Param("placeId") Long placeId);
}
