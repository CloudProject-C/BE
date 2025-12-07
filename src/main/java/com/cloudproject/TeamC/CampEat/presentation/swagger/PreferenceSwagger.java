package com.cloudproject.TeamC.CampEat.presentation.swagger;

import com.cloudproject.TeamC.CampEat.domain.User;
import com.cloudproject.TeamC.CampEat.dto.request.PreferenceRequest;
import com.cloudproject.TeamC.CampEat.dto.request.RecommendRequest;
import com.cloudproject.TeamC.CampEat.dto.response.PreferenceResponse;
import com.cloudproject.TeamC.CampEat.dto.response.QdrantSearchHit;
import com.cloudproject.TeamC.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Preference", description = "AI 기반 취향 분석 및 추천 API")
public interface PreferenceSwagger {

    @Operation(summary = "온보딩 취향 분석", description = "사용자의 초기 취향(키워드 10개)을 받아 임베딩하고 저장합니다. (비동기 처리)")
    CommonResponse<PreferenceResponse> onboarding(@AuthenticationPrincipal User user, @RequestBody PreferenceRequest request);

    @Operation(summary = "AI 실시간 추천 요청", description = "현재 기분/상황에 맞는 키워드를 받아 AI 추천 계산을 요청합니다. (비동기 처리)")
    CommonResponse<Void> recommend(@AuthenticationPrincipal User user, @RequestBody RecommendRequest request);

    @Operation(summary = "추천 결과 조회", description = "계산된 AI 추천 음식점 목록을 조회합니다.")
    CommonResponse<List<QdrantSearchHit>> getRecommendations(@AuthenticationPrincipal User user);
}