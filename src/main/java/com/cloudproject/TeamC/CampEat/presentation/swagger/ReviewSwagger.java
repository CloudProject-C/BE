package com.cloudproject.TeamC.CampEat.presentation.swagger;

import com.cloudproject.TeamC.CampEat.dto.response.ReviewResponse;
import com.cloudproject.TeamC.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Review", description = "리뷰 관련 API")
public interface ReviewSwagger {

    @Operation(summary = "리뷰 등록", description = "이미지와 함께 리뷰를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    CommonResponse<List<String>> createReview(
            @Parameter(
                    description = "리뷰 생성 요청 JSON",
                    required = true,
                    schema = @Schema(
                            type = "string",
                            description = "JSON 구조의 문자열",
                            example = """
                                    {
                                        "placeId": 243,
                                        "rating": 5,
                                        "content": "시설이 깨끗하고 경치가 너무 좋아요! 재방문 의사 있습니다."
                                    }
                                    """
                    )
            )
            @RequestPart("request") String requestJson,

            @Parameter(description = "업로드할 이미지 파일 리스트 (선택 사항)")
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    );

    @Operation(summary = "리뷰 목록 조회", description = "장소에 대한 리뷰를 조건에 맞춰 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    CommonResponse<Page<ReviewResponse>> getReviews(
            @Parameter(description = "장소 ID", example = "1")
            @PathVariable Long placeId,

            @Parameter(description = "정렬 순서 (LATEST, OLDEST, RATING_HIGH, RATING_LOW, LIKES)", example = "LATEST")
            @RequestParam(defaultValue = "LATEST") String sort,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "현재 로그인한 사용자 ID (비로그인 시 생략 가능)", hidden = true)
            @RequestParam(required = false) Long userId
    );
}