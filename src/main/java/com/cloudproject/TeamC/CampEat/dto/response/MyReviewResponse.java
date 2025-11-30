package com.cloudproject.TeamC.CampEat.dto.response;

import com.cloudproject.TeamC.CampEat.domain.Review;
import com.cloudproject.TeamC.CampEat.domain.ReviewImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "내가 작성한 리뷰 조회 응답")
public record MyReviewResponse(
        @Schema(description = "리뷰 ID", example = "10")
        Long reviewId,

        @Schema(description = "음식점 이름", example = "맛있는 식당")
        String placeName,

        @Schema(description = "별점", example = "5")
        Integer rating,

        @Schema(description = "리뷰 내용", example = "정말 맛있어요!")
        String content,

        @Schema(description = "대표 사진 URL (첫 번째 사진)", example = "https://s3.../review.jpg")
        String representativeImageUrl,

        @Schema(description = "받은 좋아요 수", example = "12")
        long likeCount
) {
    public static MyReviewResponse from(Review review, long likeCount) {
        String imageUrl = review.getImages().isEmpty() ? null : review.getImages().get(0).getImageUrl();
        return MyReviewResponse.builder()
                .reviewId(review.getId())
                .placeName(review.getPlace().getPlaceName())
                .rating(review.getRating())
                .content(review.getContent())
                .representativeImageUrl(imageUrl)
                .likeCount(likeCount)
                .build();
    }
}