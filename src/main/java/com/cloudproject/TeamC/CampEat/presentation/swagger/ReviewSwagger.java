package com.cloudproject.TeamC.CampEat.presentation.swagger;

import com.cloudproject.TeamC.CampEat.domain.User;
import com.cloudproject.TeamC.CampEat.dto.request.ReviewCreateRequest;
import com.cloudproject.TeamC.CampEat.dto.response.ReviewResponse;
import com.cloudproject.TeamC.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Review", description = "리뷰 관련 API")
public interface ReviewSwagger {

    @Operation(
            summary = "리뷰 작성",
            description = "JSON(request) + 이미지(images)를 동시에 받는 리뷰 작성 API"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    CommonResponse<List<String>> createReview(
            @RequestPart("request")
            @Parameter(
                    description = "리뷰 JSON 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReviewCreateRequest.class)
                    )
            )
            ReviewCreateRequest request,
            @RequestPart(value = "images", required = false)
            @Parameter(
                    description = "업로드 이미지 리스트",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            List<MultipartFile> images,
            @AuthenticationPrincipal User user
    );

    @Operation(summary = "리뷰 목록 조회", description = "장소에 대한 리뷰를 조건에 맞춰 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    CommonResponse<Page<ReviewResponse>> getReviews(
            @Parameter(description = "장소 ID", example = "7724465")
            @PathVariable Long placeId,

            @Parameter(description = "정렬 순서 (LATEST, OLDEST, RATING_HIGH, RATING_LOW, LIKES)", example = "LATEST")
            @RequestParam(defaultValue = "LATEST") String sort,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @AuthenticationPrincipal User user
    );

    @Operation(summary = "리뷰 좋아요 (토글)", description = "리뷰에 좋아요를 누르거나 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    CommonResponse<Void> toggleReviewLike(
            @Parameter(description = "리뷰 ID", example = "10") @PathVariable Long reviewId, @AuthenticationPrincipal User user);
}