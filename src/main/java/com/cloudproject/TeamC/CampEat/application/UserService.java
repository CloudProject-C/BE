package com.cloudproject.TeamC.CampEat.application;

import com.cloudproject.TeamC.CampEat.domain.Place;
import com.cloudproject.TeamC.CampEat.domain.PlaceLike;
import com.cloudproject.TeamC.CampEat.domain.Review;
import com.cloudproject.TeamC.CampEat.domain.User;
import com.cloudproject.TeamC.CampEat.dto.response.MyLikedPlaceResponse;
import com.cloudproject.TeamC.CampEat.dto.response.MyPageResponse;
import com.cloudproject.TeamC.CampEat.dto.response.MyReviewResponse;
import com.cloudproject.TeamC.CampEat.exception.CampEatException;
import com.cloudproject.TeamC.CampEat.exception.code.CampEatErrorCode;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.*;
import com.cloudproject.TeamC.global.util.DtoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PlaceLikeRepository placeLikeRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    // 1. 마이 페이지 조회
    public MyPageResponse getMyPageInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CampEatException(CampEatErrorCode.USER_NOT_FOUND));

        Long myReviewCount = reviewRepository.countByUserId(userId);
        Long myPlaceLikeCount = placeLikeRepository.countByUserId(userId);

        List<String> preferences = userPreferenceRepository.findByUserId(userId).stream()
                .map(up -> up.getCategory().name())
                .collect(Collectors.toList());

        return MyPageResponse.of(user, myReviewCount, myPlaceLikeCount, preferences);
    }

    // 2. 내가 작성한 리뷰 목록 조회
    public Page<MyReviewResponse> getMyReviews(Long userId, String sortType, int page, int size) {
        Pageable pageable = createReviewPageable(sortType, page, size);
        Page<Object[]> reviewPage;

        if ("LIKES".equalsIgnoreCase(sortType)) {
            reviewPage = reviewRepository.findReviewsByUserIdWithLikeCountOrderByLikesDesc(userId, PageRequest.of(page, size));
        } else {
            reviewPage = reviewRepository.findReviewsByUserIdWithLikeCount(userId, pageable);
        }

        return reviewPage.map(result -> {
            Review review = (Review) result[0];
            Long likeCount = (Long) result[1];
            return MyReviewResponse.from(review, likeCount);
        });
    }

    // 3. 내가 좋아요 누른 음식점 목록 조회
    public Page<MyLikedPlaceResponse> getMyLikedPlaces(Long userId, Pageable pageable) {
        // 1. 좋아요한 장소 목록 페이징 조회
        Page<PlaceLike> placeLikes = placeLikeRepository.findByUserId(userId, pageable);

        if (placeLikes.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2. 장소 ID 목록 추출
        List<Long> placeIds = placeLikes.getContent().stream()
                .map(pl -> pl.getPlace().getId())
                .toList();

        // 3. 배치 쿼리로 데이터 조회 (Map으로 변환)
        // 3-1. 최신 이미지
        Map<Long, String> imageMap = reviewRepository.findLatestReviewImagesByPlaceIds(placeIds).stream()
                .collect(Collectors.toMap(obj -> (Long) obj[0], obj -> (String) obj[1]));

        // 3-2. 평균 평점
        Map<Long, Double> ratingMap = reviewRepository.findAverageRatingsByPlaceIds(placeIds).stream()
                .collect(Collectors.toMap(obj -> (Long) obj[0], obj -> (Double) obj[1]));

        // 3-3. 가게 좋아요 수
        Map<Long, Long> placeLikeCountMap = placeLikeRepository.countLikesByPlaceIds(placeIds).stream()
                .collect(Collectors.toMap(obj -> (Long) obj[0], obj -> (Long) obj[1]));

        // 4. 매핑
        return placeLikes.map(placeLike -> {
            Place place = placeLike.getPlace();
            Long placeId = place.getId();

            String recentImageUrl = imageMap.get(placeId);
            Double averageRating = ratingMap.getOrDefault(placeId, 0.0);
            Long placeLikeCount = placeLikeCountMap.getOrDefault(placeId, 0L);
            String simpleCategory = DtoUtil.extractSimpleCategory(place.getCategoryName());

            return MyLikedPlaceResponse.of(place, recentImageUrl, simpleCategory, averageRating, placeLikeCount);
        });
    }

    private Pageable createReviewPageable(String sortType, int page, int size) {
        Sort sort = switch (sortType.toUpperCase()) {
            case "LATEST" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "OLDEST" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "RATING_HIGH" -> Sort.by(Sort.Direction.DESC, "rating");
            case "RATING_LOW" -> Sort.by(Sort.Direction.ASC, "rating");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        if ("LIKES".equalsIgnoreCase(sortType)) {
            return PageRequest.of(page, size);
        }

        return PageRequest.of(page, size, sort);
    }
}