package com.cloudproject.TeamC.CampEat.application;

import com.cloudproject.TeamC.CampEat.domain.Place;
import com.cloudproject.TeamC.CampEat.domain.School;
import com.cloudproject.TeamC.CampEat.domain.User;
import com.cloudproject.TeamC.CampEat.dto.response.MainPageRecommendedPlaceResponse;
import com.cloudproject.TeamC.CampEat.dto.response.MainPageResponse;
import com.cloudproject.TeamC.CampEat.dto.response.RecommendationResponse;
import com.cloudproject.TeamC.CampEat.exception.CampEatException;
import com.cloudproject.TeamC.CampEat.exception.code.CampEatErrorCode;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.PlaceJpaRepository;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.PlaceLikeRepository;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.PlaceRepository;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.ReviewRepository;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;
    private final PlaceJpaRepository placeJpaRepository;
    private final PlaceLikeRepository placeLikeRepository;
    private final PreferenceService preferenceService;

    public MainPageResponse getMainPageInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CampEatException(CampEatErrorCode.USER_NOT_FOUND));

        School school = user.getSchool();
        Long schoolId = school.getId();

        // 1. 학교 이름
        String schoolName = school.getName();

        // 2. 학교 소속 유저 수
        Long userCount = userRepository.countBySchoolId(schoolId);

        // 3. 학교 소속 리뷰 수
        Long reviewCount = reviewRepository.countBySchoolId(schoolId);

        // 4. 학교 근처 음식점 수
        Long placeCount = placeRepository.countBySchoolId(schoolId);

        // 5. 추천 맛집 Top 3 조회 및 상세 정보 매핑
        List<MainPageRecommendedPlaceResponse> recommendedPlaces = getRecommendedPlaces(userId);

        return MainPageResponse.builder()
                .schoolName(schoolName)
                .schoolUserCount(userCount)
                .schoolReviewCount(reviewCount)
                .schoolPlaceCount(placeCount)
                .recommendedPlaces(recommendedPlaces)
                .build();
    }

    private List<MainPageRecommendedPlaceResponse> getRecommendedPlaces(Long userId) {
        // AI 추천 ID 목록 조회 (Top 3)
        List<RecommendationResponse> recommendations = preferenceService.getRecommendationTop3(userId);

        if (recommendations.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> placeIds = recommendations.stream()
                .map(RecommendationResponse::getId)
                .toList();

        // 1. 장소 기본 정보 Bulk 조회
        Map<Long, Place> placeMap = placeJpaRepository.findAllById(placeIds).stream()
                .collect(Collectors.toMap(Place::getId, Function.identity()));

        // 2. 부가 정보 Bulk 조회 (평점, 이미지, 좋아요 수)
        Map<Long, Double> ratingMap = reviewRepository.findAverageRatingsByPlaceIds(placeIds).stream()
                .collect(Collectors.toMap(obj -> (Long) obj[0], obj -> (Double) obj[1]));

        Map<Long, String> imageMap = reviewRepository.findLatestReviewImagesByPlaceIds(placeIds).stream()
                .collect(Collectors.toMap(obj -> (Long) obj[0], obj -> (String) obj[1]));

        Map<Long, Long> likeCountMap = placeLikeRepository.countLikesByPlaceIds(placeIds).stream()
                .collect(Collectors.toMap(obj -> (Long) obj[0], obj -> (Long) obj[1]));

        // 3. 내가 좋아요 한 여부 조회
        Set<Long> myLikedPlaceIds = new HashSet<>(placeLikeRepository.findLikedPlaceIds(userId, placeIds));

        // 4. DTO 조립 (추천 순서 유지)
        List<MainPageRecommendedPlaceResponse> results = new ArrayList<>();
        for (RecommendationResponse rec : recommendations) {
            Place place = placeMap.get(rec.getId());
            if (place != null) {
                Double rating = ratingMap.getOrDefault(place.getId(), 0.0);
                String imageUrl = imageMap.get(place.getId());
                Long likeCount = likeCountMap.getOrDefault(place.getId(), 0L);
                boolean isLiked = myLikedPlaceIds.contains(place.getId());

                // score(0.0 ~ 1.0) -> percent(0 ~ 100)
                int preferencePercent = (int) (rec.getScore() * 100);

                results.add(MainPageRecommendedPlaceResponse.of(
                        place, rating, likeCount, isLiked, imageUrl, preferencePercent
                ));
            }
        }
        return results;
    }
}