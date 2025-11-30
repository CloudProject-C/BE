package com.cloudproject.TeamC.CampEat.dto.response;

import com.cloudproject.TeamC.CampEat.domain.Place;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "가게 상세 정보 응답")
public record PlaceDetailResponse(
        @Schema(description = "장소 ID", example = "7724465")
        Long placeId,

        @Schema(description = "장소 이름", example = "달콤한위로 경희대국제캠퍼스점")
        String placeName,

        @Schema(description = "카테고리 이름", example = "음식점 > 카페 > 테마카페 > 디저트카페")
        String categoryName,

        @Schema(description = "전화번호", example = "031-1234-5678")
        String phone,

        @Schema(description = "지번 주소", example = "경기도 용인시 ...")
        String addressName,

        @Schema(description = "도로명 주소", example = "경기도 용인시 ...")
        String roadAddressName,

        @Schema(description = "거리 (미터 단위)", example = "500")
        Integer distance,

        @Schema(description = "장소 URL", example = "http://place.map.kakao.com/...")
        String placeUrl,

        @Schema(description = "평균 평점 (0.0 ~ 5.0)", example = "4.5")
        Double averageRating,

        @Schema(description = "총 리뷰 수", example = "120")
        Long reviewCount,

        @Schema(description = "선호도 퍼센트 (Qdrant 코사인 유사도)", example = "null")
        Integer preferencePercent,

        @Schema(description = "장소 좋아요(찜) 수", example = "5")
        Long placeLikeCount,

        @Schema(description = "내가 좋아요 한 여부", example = "true")
        boolean isLiked
) {
    public static PlaceDetailResponse of(Place place, Double averageRating, Long reviewCount, Integer calculatedDistance, Long placeLikeCount, boolean isLiked) {
        // 평점이 없으면 0.0 처리
        double rating = (averageRating != null) ? averageRating : 0.0;

        return PlaceDetailResponse.builder()
                .placeId(place.getId())
                .placeName(place.getPlaceName())
                .categoryName(extractSimpleCategory(place.getCategoryName()))
                .phone(place.getPhone())
                .addressName(place.getAddressName())
                .roadAddressName(place.getRoadAddressName())
                .distance(calculatedDistance)
                .placeUrl(place.getPlaceUrl())
                .averageRating(Math.round(rating * 10.0) / 10.0)
                .reviewCount(reviewCount)
                .preferencePercent(null) // TODO: 추후 Qdrant 검색 결과와 연동하여 값 주입 필요
                .placeLikeCount(placeLikeCount)
                .isLiked(isLiked)
                .build();
    }

    private static String extractSimpleCategory(String fullCategoryName) {
        if (fullCategoryName == null || fullCategoryName.isBlank()) {
            return "기타";
        }
        // " > " 기준으로 문자열 분리
        String[] categories = fullCategoryName.split(" > ");

        // 길이가 2 이상이면 두 번째(인덱스 1) 요소 반환
        // 예: "음식점 > 카페 > 테마카페" -> "카페"
        if (categories.length > 1) {
            return categories[1];
        }

        // " > "가 없거나 구조가 다르면 전체 반환
        return fullCategoryName;
    }
}
