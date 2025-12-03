package com.cloudproject.TeamC.CampEat.presentation.swagger;

import com.cloudproject.TeamC.CampEat.domain.User;
import com.cloudproject.TeamC.CampEat.dto.response.MyLikedPlaceResponse;
import com.cloudproject.TeamC.CampEat.dto.response.MyPageResponse;
import com.cloudproject.TeamC.CampEat.dto.response.MyReviewResponse;
import com.cloudproject.TeamC.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User (MyPage)", description = "마이 페이지 및 사용자 활동 API")
public interface UserSwagger {

    @Operation(summary = "마이 페이지 조회", description = "내 프로필 정보, 활동 통계(리뷰 수, 찜 수), 선호 카테고리를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    CommonResponse<MyPageResponse> getMyPage(@AuthenticationPrincipal User user);

    @Operation(summary = "내가 작성한 리뷰 목록 조회", description = "내가 작성한 리뷰를 조건에 맞춰 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    CommonResponse<Page<MyReviewResponse>> getMyReviews(
            @Parameter(description = "정렬 순서 (LATEST, OLDEST, RATING_HIGH, RATING_LOW, LIKES)", example = "LATEST")
            @RequestParam(defaultValue = "LATEST") String sort,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user
    );

    @Operation(summary = "내가 좋아요한 음식점 목록 조회", description = "내가 찜한 음식점을 최신순(찜한 순서)으로 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    CommonResponse<Page<MyLikedPlaceResponse>> getMyLikedPlaces(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user
    );
}