package com.cloudproject.TeamC.CampEat.application;

import com.cloudproject.TeamC.CampEat.dto.response.QdrantSearchHit;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserSessionRecommendationStore {
    private final Map<Long, float[]> newEmbeddingStore = new ConcurrentHashMap<>();

    private final Map<Long, List<QdrantSearchHit>> store = new ConcurrentHashMap<>();
    public void saveUserNewEmbedding(Long userId, float[] embedding) {
        newEmbeddingStore.put(userId, embedding);
    }

    public float[] getUserNewEmbedding(Long userId) {
        return newEmbeddingStore.get(userId);
    }

    public void saveUserRecommendations(Long userId, List<QdrantSearchHit> hits) {
        store.put(userId, hits);
    }

    public List<QdrantSearchHit> getUserRecommendations(Long userId) {
        return store.getOrDefault(userId, List.of());
    }

    public void clearUserRecommendations(Long userId) {
        store.remove(userId);
    }
}
