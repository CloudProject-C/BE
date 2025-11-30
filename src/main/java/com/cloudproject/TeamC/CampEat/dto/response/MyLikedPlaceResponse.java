package com.cloudproject.TeamC.CampEat.dto.response;

import com.cloudproject.TeamC.CampEat.domain.Place;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "내가 좋아요한 음식점 조회 응답")
public record MyLikedPlaceResponse(
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
        Long placeLikeCount
) {
    public static MyLikedPlaceResponse of(
            Place place,
            String recentImageUrl,
            String simpleCategoryName,
            Double averageRating,
            Long placeLikeCount
    ) {
        return MyLikedPlaceResponse.builder()
                .placeId(place.getId())
                .placeName(place.getPlaceName())
                .categoryName(simpleCategoryName)
                .recentImageUrl(recentImageUrl)
                .averageRating(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0)
                .placeLikeCount(placeLikeCount)
                .build();
    }
}