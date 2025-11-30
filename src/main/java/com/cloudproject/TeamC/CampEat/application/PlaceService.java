package com.cloudproject.TeamC.CampEat.application;

import com.cloudproject.TeamC.CampEat.domain.FoodCategory;
import com.cloudproject.TeamC.CampEat.domain.Place;
import com.cloudproject.TeamC.CampEat.dto.response.PlaceDetailResponse;
import com.cloudproject.TeamC.CampEat.dto.response.PlaceMapResponse;
import com.cloudproject.TeamC.CampEat.exception.CampEatException;
import com.cloudproject.TeamC.CampEat.exception.code.CampEatErrorCode;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.PlaceLikeRepository;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.PlaceRepository;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.ReviewRepository;
import com.cloudproject.TeamC.global.util.LocationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final PlaceLikeRepository placeLikeRepository;

    private static final int WGS84_SRID = 4326;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), WGS84_SRID);

    public PlaceDetailResponse getPlaceDetail(Long placeId, Double userLat, Double userLon) {
        // 1. 장소 조회
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new CampEatException(CampEatErrorCode.PLACE_NOT_FOUND));

        // 2. 리뷰 통계 조회
        Double averageRating = reviewRepository.findAverageRatingByPlaceId(placeId);
        Long reviewCount = reviewRepository.countByPlaceIdAndIsHiddenFalse(placeId);

        // 3. 거리 계산 (사용자 위치가 제공된 경우)
        Integer calculatedDistance = place.getDistance(); // 기본값

        if (userLat != null && userLon != null && place.getLocation() != null) {
            // 사용자 위치를 Point 객체로 변환 (x: 경도, y: 위도 순서 주의)
            Point userPoint = geometryFactory.createPoint(new Coordinate(userLon, userLat));

            // LocationUtil을 사용하여 거리 계산 (KM -> Meter 변환)
            double distanceKm = LocationUtil.calculateDistance(userPoint, place.getLocation());
            calculatedDistance = (int) (distanceKm * 1000);
        }
        log.info("User: lat={}, lon={}", userLat, userLon);
        log.info("Place: lat(Y)={}, lon(X)={}", place.getLocation().getY(), place.getLocation().getX());
        // 4. 반환
        return PlaceDetailResponse.of(place, averageRating, reviewCount, calculatedDistance);
    }

    public List<PlaceMapResponse> getPlacesNearby(Double lat, Double lon, Double radius, String sort, FoodCategory category) {
        // 1. 사용자 위치 Point 생성
        Point userPoint = geometryFactory.createPoint(new Coordinate(lon, lat));

        // 2. 카테고리 Enum -> 검색 키워드 변환 (예: KOREAN -> "한식")
        String categoryKeyword = (category != null) ? mapCategoryToKeyword(category) : null;

        // 3. 반경 내 장소 조회 (DB)
        List<Place> places = placeRepository.findPlacesNearby(userPoint, radius, categoryKeyword);

        // 4. 데이터 가공 (거리 계산 및 이미지 매핑)
        List<PlaceMapResponse> responses = places.stream()
                .map(place -> {
                    // 거리 계산
                    double distanceKm = LocationUtil.calculateDistance(userPoint, place.getLocation());
                    int distanceM = (int) (distanceKm * 1000);

                    // 최신 리뷰 이미지 조회 (LIMIT 1)
                    List<String> images = reviewRepository.findLatestReviewImageByPlaceId(place.getId(), PageRequest.of(0, 1));
                    String imageUrl = images.isEmpty() ? null : images.get(0);

                    // 정렬을 위한 추가 정보 조회 (필요 시 Batch Fetch 최적화 고려)
                    Long reviewCount = reviewRepository.countByPlaceIdAndIsHiddenFalse(place.getId());
                    Double rating = reviewRepository.findAverageRatingByPlaceId(place.getId());
                    if (rating == null) rating = 0.0;

                    long placeLikeCount = placeLikeRepository.countByPlace(place);

                    return PlaceMapResponse.of(place, distanceM, imageUrl, reviewCount, rating, placeLikeCount);
                })
                .collect(Collectors.toList());

        // 5. 정렬 (Java Stream)
        // 정렬 기준: LATEST(거리순?), LIKES(평점순/리뷰순?), REVIEW(리뷰많은순)
        if ("LIKES".equalsIgnoreCase(sort) || "RATING".equalsIgnoreCase(sort)) {
            // 평점 높은 순 -> 리뷰 많은 순
            responses.sort(Comparator.comparing(PlaceMapResponse::rating).reversed()
                    .thenComparing(Comparator.comparing(PlaceMapResponse::reviewCount).reversed()));
        } else if ("REVIEW".equalsIgnoreCase(sort)) {
            // 리뷰 많은 순
            responses.sort(Comparator.comparing(PlaceMapResponse::reviewCount).reversed());
        } else {
            // 기본: 거리 가까운 순 (LATEST 등)
            responses.sort(Comparator.comparing(PlaceMapResponse::distance));
        }

        // TODO: AI 추천순은 추후 구현

        return responses;
    }

    private String mapCategoryToKeyword(FoodCategory category) {
        return switch (category) {
            case KOREAN -> "한식";
            case JAPANESE -> "일식";
            case CHINESE -> "중식";
            case WESTERN -> "양식";
            case CAFE -> "카페";
            case DESSERT -> "디저트";
            // 필요에 따라 추가 매핑
        };
    }
}