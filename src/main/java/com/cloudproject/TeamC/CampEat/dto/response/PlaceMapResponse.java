package com.cloudproject.TeamC.CampEat.dto.response;

import com.cloudproject.TeamC.CampEat.domain.Place;
import com.cloudproject.TeamC.global.util.DtoUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "지도/리스트용 음식점 요약 정보")
public record PlaceMapResponse(
        @Schema(description = "장소 ID", example = "1")
        Long placeId,

        @Schema(description = "장소 이름", example = "맛있는 식당")
        String placeName,

        @Schema(description = "카테고리", example = "한식")
        String categoryName,

        @Schema(description = "위도(y)", example = "37.1234")
        Double latitude,

        @Schema(description = "경도(x)", example = "127.1234")
        Double longitude,

        @Schema(description = "내 위치로부터의 거리(미터)", example = "150")
        Integer distance,

        @Schema(description = "대표 이미지(최신 리뷰 이미지)", example = "https://s3.../image.jpg")
        String imageUrl,

        @Schema(description = "리뷰 수(정렬용)", example = "10")
        Long reviewCount,

        @Schema(description = "평점(정렬용)", example = "4.5")
        Double rating,

        @Schema(description = "장소 좋아요(찜) 수", example = "5")
        Long placeLikeCount,

        @Schema(description = "내가 좋아요 한 여부", example = "true")
        boolean isLiked
) {
    public static PlaceMapResponse of(Place place, Integer distance, String imageUrl, Long reviewCount, Double rating, Long placeLikeCount, boolean isLiked) {
        return PlaceMapResponse.builder()
                .placeId(place.getId())
                .placeName(place.getPlaceName())
                .categoryName(DtoUtil.extractSimpleCategory(place.getCategoryName()))
                .latitude(place.getLocation().getY()) // Y가 위도
                .longitude(place.getLocation().getX()) // X가 경도
                .distance(distance)
                .imageUrl(imageUrl)
                .reviewCount(reviewCount)
                .rating(rating)
                .placeLikeCount(placeLikeCount)
                .isLiked(isLiked)
                .build();
    }
}