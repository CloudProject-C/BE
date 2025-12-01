package com.cloudproject.TeamC.CampEat.dto.response;

import com.cloudproject.TeamC.CampEat.domain.Review;
import com.cloudproject.TeamC.CampEat.domain.ReviewImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Schema(description = "리뷰 상세 조회 응답")
public record ReviewResponse(
        @Schema(description = "리뷰 ID", example = "1")
        Long reviewId,

        @Schema(description = "작성자 닉네임", example = "캠핑마스터")
        String nickname,

        @Schema(description = "리뷰 작성 시간", example = "2023-11-27T14:30:00")
        LocalDateTime createdAt,

        @Schema(description = "리뷰 내용", example = "시설이 깨끗하고 음식이 맛있어요!")
        String content,

        @Schema(description = "평점 (1~5)", example = "5")
        Integer rating,

        @Schema(description = "리뷰 이미지 URL 목록")
        List<String> imageUrls,

        @Schema(description = "좋아요 수", example = "10")
        long likeCount,

        @Schema(description = "본인이 작성한 리뷰인지 여부", example = "true")
        boolean isMyReview,

        @Schema(description = "내가 좋아요 누른 여부", example = "true")
        boolean isLiked
) {
    public static ReviewResponse from(Review review, long likeCount, Long currentUserId,  boolean isLiked) {
        return ReviewResponse.builder()
                .reviewId(review.getId())
                .nickname(review.getUser().getNickname())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .likeCount(likeCount)
                .isMyReview(currentUserId != null && review.getUser().getId().equals(currentUserId))
                .imageUrls(review.getImages().stream()
                        .map(ReviewImage::getImageUrl)
                        .collect(Collectors.toList()))
                .isLiked(isLiked)
                .build();
    }
}