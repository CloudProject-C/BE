package com.cloudproject.TeamC.CampEat.presentation;

import com.cloudproject.TeamC.CampEat.application.PreferenceService;
import com.cloudproject.TeamC.CampEat.domain.User;
import com.cloudproject.TeamC.CampEat.dto.request.PreferenceRequest;
import com.cloudproject.TeamC.CampEat.dto.request.RecommendRequest;
import com.cloudproject.TeamC.CampEat.dto.response.PreferenceResponse;
import com.cloudproject.TeamC.CampEat.dto.response.QdrantSearchHit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/preference")
public class PreferenceController {

    private final PreferenceService preferenceService;


    @PostMapping("/onboarding")
    public ResponseEntity<PreferenceResponse> onboarding(@AuthenticationPrincipal User user, @RequestBody PreferenceRequest request) {
        Long userId = user.getId();
        // 비동기로 임베딩 + Qdrant 저장
        preferenceService.processOnboardingAsync(request,userId);

        // 사용자는 즉시 다음 서비스로 이동 가능
        return ResponseEntity.accepted()
                .body(new PreferenceResponse("온보딩 정보 수신 완료. 백그라운드에서 저장 중입니다."));
    }

    @PostMapping("/recommend")
    public ResponseEntity<String> recommend(@AuthenticationPrincipal User user,@RequestBody RecommendRequest request) {
        Long userId = user.getId();
        preferenceService.recommendAsync(request, userId);

        return ResponseEntity.accepted()
                .body("추천 계산을 백그라운드에서 수행합니다.");
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<QdrantSearchHit>> getRecommendations(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        List<QdrantSearchHit> result = preferenceService.getRecommendations(userId);
        return ResponseEntity.ok(result);
    }
}
