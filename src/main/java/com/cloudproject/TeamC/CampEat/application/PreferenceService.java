package com.cloudproject.TeamC.CampEat.application;

import com.cloudproject.TeamC.CampEat.dto.request.PreferenceRequest;
import com.cloudproject.TeamC.CampEat.dto.request.RecommendRequest;
import com.cloudproject.TeamC.CampEat.dto.response.QdrantSearchHit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class PreferenceService {

    private final GeminiApiService geminiApiService;
    private final QdrantService qdrantService;
    private final UserSessionRecommendationStore recommendationStore;


    @Async("taskExecutor")
    public void processOnboardingAsync(PreferenceRequest request, Long userId) {
        List<String> features = request.getFeatures();

        try {
            String text = String.join(" ", features);

            float[] embedding = geminiApiService.getEmbeddingFromGemini(text);

            qdrantService.createCollectionIfNotExists("onboarding",3072);
            qdrantService.saveOnboardingEmbedding(userId, embedding, features);

        } catch (Exception e) {
            log.error("💥 온보딩 처리 중 에러 userId={}", userId, e);
        }
    }
    @Async("taskExecutor")
    public void recommendAsync(RecommendRequest request, Long userId) {
        List<String> newFeatures = request.getFeatures();

        String newText = String.join(" ", newFeatures);
        float[] newEmbedding = geminiApiService.getEmbeddingFromGemini(newText);
        recommendationStore.saveUserNewEmbedding(userId, newEmbedding);

        float[] oldEmbedding = qdrantService.getOnboardingEmbedding(userId);


        if (oldEmbedding == null) {
            qdrantService.saveOnboardingEmbedding(userId, newEmbedding, newFeatures);
            oldEmbedding = newEmbedding;
        }

        float[] combined = new float[newEmbedding.length];
        float alpha = 0.7f;
        float beta = 0.3f;
        for (int i = 0; i < newEmbedding.length; i++) {
            combined[i] = alpha * newEmbedding[i] + beta * oldEmbedding[i];
        }

        List<QdrantSearchHit> top = qdrantService.searchTopNInRestaurants(combined, 1000);
        recommendationStore.saveUserRecommendations(userId, top);
    }

    public Double getSimilarityForUserAndRestaurant(Long userId, Long restaurantId) {
        float[] newEmbedding = recommendationStore.getUserNewEmbedding(userId);
        if (newEmbedding == null) return null;

        float[] oldEmbedding = qdrantService.getOnboardingEmbedding(userId);
        if (oldEmbedding == null) oldEmbedding = newEmbedding;

        float[] combined = new float[newEmbedding.length];
        float alpha = 0.7f;
        float beta = 0.3f;
        for (int i = 0; i < newEmbedding.length; i++) {
            combined[i] = alpha * newEmbedding[i] + beta * oldEmbedding[i];
        }

        return qdrantService.searchSimilarityForRestaurant(combined, restaurantId);
    }



    public List<QdrantSearchHit> getRecommendations(Long userId) {
        return recommendationStore.getUserRecommendations(userId);
    }
}
