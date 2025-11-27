package com.cloudproject.TeamC.CampEat.presentation.swagger;

import com.cloudproject.TeamC.CampEat.dto.response.PlaceDetailResponse;
import com.cloudproject.TeamC.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Place", description = "장소 상세 정보 API")
public interface PlaceSwagger {

    @Operation(summary = "장소 상세 조회", description = "가게 ID와 사용자 위치를 통해 상세 정보와 거리를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 장소 ID")
    })
    CommonResponse<PlaceDetailResponse> getPlaceDetail(
            @Parameter(description = "장소 ID", example = "1", required = true)
            @PathVariable Long placeId,

            @Parameter(description = "사용자 위도 (Latitude)", example = "37.5665")
            @RequestParam(required = false) Double latitude,

            @Parameter(description = "사용자 경도 (Longitude)", example = "126.9780")
            @RequestParam(required = false) Double longitude
    );
}
