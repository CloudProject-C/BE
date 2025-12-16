package com.cloudproject.TeamC.CampEat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "메인 페이지 정보 응답")
public record MainPageResponse(
        @Schema(description = "사용자 소속 학교 이름", example = "경희대학교")
        String schoolName,

        @Schema(description = "학교 소속 유저 수", example = "1500")
        Long schoolUserCount,

        @Schema(description = "학교 소속 리뷰 수", example = "3200")
        Long schoolReviewCount,

        @Schema(description = "학교 근처 음식점 수 (반경 2km 이내)", example = "450")
        Long schoolPlaceCount,

        @Schema(description = "AI 추천 맛집 목록 (Top 3)")
        List<MainPageRecommendedPlaceResponse> recommendedPlaces
) {}