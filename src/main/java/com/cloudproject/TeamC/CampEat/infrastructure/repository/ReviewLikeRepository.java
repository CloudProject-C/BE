package com.cloudproject.TeamC.CampEat.infrastructure.repository;

import com.cloudproject.TeamC.CampEat.domain.Review;
import com.cloudproject.TeamC.CampEat.domain.ReviewLike;
import com.cloudproject.TeamC.CampEat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    long countByReviewId(Long reviewId);

    Optional<ReviewLike> findByReviewAndUser(Review review, User user);
}
