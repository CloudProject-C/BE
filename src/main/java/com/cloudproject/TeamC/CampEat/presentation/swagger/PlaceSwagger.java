package com.cloudproject.TeamC.CampEat.presentation.swagger;

import com.cloudproject.TeamC.CampEat.domain.FoodCategory;
import com.cloudproject.TeamC.CampEat.dto.response.PlaceDetailResponse;
import com.cloudproject.TeamC.CampEat.dto.response.PlaceMapResponse;
import com.cloudproject.TeamC.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Place", description = "장소 상세 정보 API")
public interface PlaceSwagger {

    @Operation(summary = "장소 상세 조회", description = "가게 ID와 사용자 위치를 통해 상세 정보와 거리를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 장소 ID")
    })
    CommonResponse<PlaceDetailResponse> getPlaceDetail(
            @Parameter(description = "장소 ID", example = "7724465", required = true)
            @PathVariable Long placeId,

            @Parameter(description = "사용자 위도 (Latitude)", example = "37.251")
            @RequestParam(required = false) Double latitude,

            @Parameter(description = "사용자 경도 (Longitude)", example = "127.078")
            @RequestParam(required = false) Double longitude,

            @Parameter(description = "유저 ID")
            @RequestParam(required = false) Long userId
    );

    @Operation(summary = "내 주변 음식점 조회 (지도/리스트)", description = "현재 위치 기준 반경 내 음식점을 필터링하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    CommonResponse<List<PlaceMapResponse>> getPlacesNearby(
            @Parameter(description = "사용자 위도", example = "37.2479", required = true)
            @RequestParam Double latitude,

            @Parameter(description = "사용자 경도", example = "127.0772", required = true)
            @RequestParam Double longitude,

            @Parameter(description = "반경 (미터 단위)", example = "150")
            @RequestParam(defaultValue = "150") Double radius,

            @Parameter(description = "정렬 (DISTANCE: 거리순, LIKES: 평점순, REVIEW: 리뷰많은순)", example = "DISTANCE")
            @RequestParam(defaultValue = "DISTANCE") String sort,

            @Parameter(description = "카테고리 필터 (KOREAN, WESTERN 등)")
            @RequestParam(required = false) FoodCategory category,

            @Parameter(description = "유저 ID")
            @RequestParam(required = false) Long userId
    );

    @Operation(summary = "음식점 찜하기 (토글)", description = "음식점을 찜하거나 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    CommonResponse<Void> togglePlaceLike(
            @Parameter(description = "장소 ID", example = "1") @PathVariable Long placeId,
            @Parameter(description = "유저 ID") @RequestParam Long userId
    );
}
