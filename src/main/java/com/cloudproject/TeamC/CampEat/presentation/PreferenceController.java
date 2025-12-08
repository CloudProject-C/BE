package com.cloudproject.TeamC.CampEat.presentation;

import com.cloudproject.TeamC.CampEat.application.PreferenceService;
import com.cloudproject.TeamC.CampEat.domain.User;
import com.cloudproject.TeamC.CampEat.dto.request.PreferenceRequest;
import com.cloudproject.TeamC.CampEat.dto.request.RecommendRequest;
import com.cloudproject.TeamC.CampEat.dto.response.PreferenceResponse;
import com.cloudproject.TeamC.CampEat.dto.response.QdrantSearchHit;
import com.cloudproject.TeamC.CampEat.presentation.swagger.PreferenceSwagger;
import com.cloudproject.TeamC.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cloudproject.TeamC.global.common.code.SuccessCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/preference")
public class PreferenceController implements PreferenceSwagger {

    private final PreferenceService preferenceService;

    @Override
    @PostMapping("/onboarding")
    public CommonResponse<PreferenceResponse> onboarding(@AuthenticationPrincipal User user, @RequestBody PreferenceRequest request) {
        Long userId = user.getId();
        // 비동기로 임베딩 + Qdrant 저장
        preferenceService.processOnboardingAsync(request, userId);

        // 사용자는 즉시 다음 서비스로 이동 가능 (202 Accepted 의미의 SuccessCode 사용)
        return CommonResponse.success(PREFERENCE_ONBOARDING_SUCCESS,
                new PreferenceResponse("온보딩 정보 수신 완료. 백그라운드에서 저장 중입니다."));
    }

    @Override
    @PostMapping("/recommend")
    public CommonResponse<Void> recommend(@AuthenticationPrincipal User user, @RequestBody RecommendRequest request) {
        Long userId = user.getId();
        preferenceService.recommendAsync(request, userId);

        return CommonResponse.success(RECOMMEND_REQUEST_SUCCESS);
    }

    @Override
    @GetMapping("/recommendations")
    public CommonResponse<List<QdrantSearchHit>> getRecommendations(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        List<QdrantSearchHit> result = preferenceService.getRecommendations(userId);
        return CommonResponse.success(FETCH_RECOMMENDATION_SUCCESS, result);
    }
}
