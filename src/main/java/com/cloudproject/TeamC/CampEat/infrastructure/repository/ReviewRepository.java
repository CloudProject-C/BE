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

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.place.id = :placeId AND r.isHidden = false")
    Double findAverageRatingByPlaceId(@Param("placeId") Long placeId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.place.id = :placeId AND r.isHidden = false")
    Long countByPlaceIdAndIsHiddenFalse(@Param("placeId") Long placeId);

    // 배치: 리뷰 개수 (placeId별 그룹핑)
    @Query("SELECT r.place.id, COUNT(r) " +
            "FROM Review r " +
            "WHERE r.place.id IN :placeIds AND r.isHidden = false " +
            "GROUP BY r.place.id")
    List<Object[]> countReviewsByPlaceIds(@Param("placeIds") List<Long> placeIds);

    // 배치: 평균 평점 (placeId별 그룹핑)
    @Query("SELECT r.place.id, AVG(r.rating) " +
            "FROM Review r " +
            "WHERE r.place.id IN :placeIds AND r.isHidden = false " +
            "GROUP BY r.place.id")
    List<Object[]> findAverageRatingsByPlaceIds(@Param("placeIds") List<Long> placeIds);

    // 배치: 각 장소별 최신 이미지 1개 조회 (Native Query 사용 권장)
    @Query(value =
            "SELECT t.place_id, t.image_url " +
                    "FROM (" +
                    "    SELECT r.place_id, ri.image_url, " +
                    "           ROW_NUMBER() OVER(PARTITION BY r.place_id ORDER BY r.created_at DESC) as rn " +
                    "    FROM review r " +
                    "    JOIN review_image ri ON r.id = ri.review_id " +
                    "    WHERE r.place_id IN :placeIds AND r.is_hidden = false " +
                    ") t " +
                    "WHERE t.rn = 1",
            nativeQuery = true)
    List<Object[]> findLatestReviewImagesByPlaceIds(@Param("placeIds") List<Long> placeIds);
}
