package com.cloudproject.TeamC.CampEat.presentation;

import com.cloudproject.TeamC.CampEat.application.PreferenceService;
import com.cloudproject.TeamC.CampEat.dto.request.PreferenceRequest;
import com.cloudproject.TeamC.CampEat.dto.request.RecommendRequest;
import com.cloudproject.TeamC.CampEat.dto.response.PreferenceResponse;
import com.cloudproject.TeamC.CampEat.dto.response.QdrantSearchHit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/preference")
public class PreferenceController {

    private final PreferenceService preferenceService;


    @PostMapping("/onboarding")
    public ResponseEntity<PreferenceResponse> onboarding(@RequestBody PreferenceRequest request) {

        // 비동기로 임베딩 + Qdrant 저장
        preferenceService.processOnboardingAsync(request);

        // 사용자는 즉시 다음 서비스로 이동 가능
        return ResponseEntity.accepted()
                .body(new PreferenceResponse("온보딩 정보 수신 완료. 백그라운드에서 저장 중입니다."));
    }

    @PostMapping("/recommend")
    public ResponseEntity<String> recommend(@RequestBody RecommendRequest request) {

        preferenceService.recommendAsync(request);

        return ResponseEntity.accepted()
                .body("추천 계산을 백그라운드에서 수행합니다.");
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<QdrantSearchHit>> getRecommendations(@RequestParam Long userId) {
        List<QdrantSearchHit> result = preferenceService.getRecommendations(userId);
        return ResponseEntity.ok(result);
    }
}
