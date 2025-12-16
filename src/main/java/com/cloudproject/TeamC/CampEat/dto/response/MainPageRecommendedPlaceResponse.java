package com.cloudproject.TeamC.CampEat.dto.response;

import com.cloudproject.TeamC.CampEat.domain.Place;
import com.cloudproject.TeamC.global.util.DtoUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "메인 페이지 추천 맛집 정보")
public record MainPageRecommendedPlaceResponse(
        @Schema(description = "장소 ID", example = "100")
        Long placeId,

        @Schema(description = "음식점 이름", example = "감성 카페")
        String placeName,

        @Schema(description = "카테고리", example = "카페")
        String categoryName,

        @Schema(description = "최근 리뷰 사진 URL", example = "https://s3.../food.jpg")
        String recentImageUrl,

        @Schema(description = "평균 평점", example = "4.5")
        Double averageRating,

        @Schema(description = "가게 좋아요(찜) 수", example = "15")
        Long placeLikeCount,

        @Schema(description = "내가 좋아요 한 여부", example = "true")
        boolean isLiked,

        @Schema(description = "선호도 퍼센트", example = "95")
        Integer preferencePercent
) {
    public static MainPageRecommendedPlaceResponse of(
            Place place,
            Double averageRating,
            Long placeLikeCount,
            boolean isLiked,
            String recentImageUrl,
            Integer preferencePercent
    ) {
        return MainPageRecommendedPlaceResponse.builder()
                .placeId(place.getId())
                .placeName(place.getPlaceName())
                .categoryName(DtoUtil.extractSimpleCategory(place.getCategoryName()))
                .recentImageUrl(recentImageUrl)
                .averageRating(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0)
                .placeLikeCount(placeLikeCount)
                .isLiked(isLiked)
                .preferencePercent(preferencePercent)
                .build();
    }
}