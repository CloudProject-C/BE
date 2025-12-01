package com.cloudproject.TeamC.CampEat.application;


import com.cloudproject.TeamC.CampEat.infrastructure.persistence.PlaceRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import com.cloudproject.TeamC.CampEat.domain.FoodCategory;
import com.cloudproject.TeamC.CampEat.domain.Place;
import com.cloudproject.TeamC.CampEat.domain.PlaceLike;
import com.cloudproject.TeamC.CampEat.domain.User;
import com.cloudproject.TeamC.CampEat.dto.response.PlaceDetailResponse;
import com.cloudproject.TeamC.CampEat.dto.response.PlaceMapResponse;
import com.cloudproject.TeamC.CampEat.exception.CampEatException;
import com.cloudproject.TeamC.CampEat.exception.code.CampEatErrorCode;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.PlaceLikeRepository;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.PlaceRepository;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.ReviewRepository;
import com.cloudproject.TeamC.CampEat.infrastructure.repository.UserRepository;
import com.cloudproject.TeamC.global.util.LocationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final PlaceLikeRepository placeLikeRepository;
    private final UserRepository userRepository;
    private final GeminiApiService geminiApiService;
    private final QdrantService qdrantService;

    private static final int WGS84_SRID = 4326;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), WGS84_SRID);

    public PlaceDetailResponse getPlaceDetail(Long placeId, Double userLat, Double userLon, Long userId) {
        // 1. 장소 조회
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new CampEatException(CampEatErrorCode.PLACE_NOT_FOUND));

        // 2. 리뷰 통계 조회
        Double averageRating = reviewRepository.findAverageRatingByPlaceId(placeId);
        Long reviewCount = reviewRepository.countByPlaceIdAndIsHiddenFalse(placeId);

        long likeCount = placeLikeRepository.countByPlace(place);
        boolean isLiked = false;
        if (userId != null) {
            isLiked = placeLikeRepository.existsByPlace_IdAndUser_Id(placeId, userId);
        }

        // 3. 거리 계산 (사용자 위치가 제공된 경우)
        Integer calculatedDistance = place.getDistance();

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
        return PlaceDetailResponse.of(place, averageRating, reviewCount, calculatedDistance, likeCount, isLiked);
    }

    public List<PlaceMapResponse> getPlacesNearby(Long userId, Double lat, Double lon, Double radius, String sort, FoodCategory category) {
        // 1. 사용자 위치 Point 생성
        Point userPoint = geometryFactory.createPoint(new Coordinate(lon, lat));

        // 2. 카테고리 Enum -> 검색 키워드 변환 (예: KOREAN -> "한식")
        String categoryKeyword = (category != null) ? mapCategoryToKeyword(category) : null;

        // 3. 반경 내 장소 조회 (DB)
        List<Place> places = placeRepository.findPlacesNearby(userPoint, radius, categoryKeyword);

        if (places.isEmpty()) {
            return List.of();
        }

        List<Long> placeIds = places.stream().map(Place::getId).toList();

        // 4. 배치 쿼리 실행 (Sub Queries 4~5회)
        // 4-1. 리뷰 개수 Map 생성
        Map<Long, Long> reviewCountMap = reviewRepository.countReviewsByPlaceIds(placeIds).stream()
                .collect(Collectors.toMap(obj -> (Long) obj[0], obj -> (Long) obj[1]));

        // 4-2. 평균 평점 Map 생성
        Map<Long, Double> ratingMap = reviewRepository.findAverageRatingsByPlaceIds(placeIds).stream()
                .collect(Collectors.toMap(obj -> (Long) obj[0], obj -> (Double) obj[1]));

        // 4-3. 최신 이미지 Map 생성
        Map<Long, String> imageMap = reviewRepository.findLatestReviewImagesByPlaceIds(placeIds).stream()
                .collect(Collectors.toMap(obj -> (Long) obj[0], obj -> (String) obj[1]));

        // 4-4. 장소 찜 개수 Map 생성
        Map<Long, Long> placeLikeCountMap = placeLikeRepository.countLikesByPlaceIds(placeIds).stream()
                .collect(Collectors.toMap(obj -> (Long) obj[0], obj -> (Long) obj[1]));

        Set<Long> myLikedPlaceIds = new HashSet<>();
        if (userId != null) {
            myLikedPlaceIds.addAll(placeLikeRepository.findLikedPlaceIds(userId, placeIds));
        }

        return places.stream()
                .map(place -> {
                    double distanceKm = LocationUtil.calculateDistance(userPoint, place.getLocation());
                    int distanceM = (int) (distanceKm * 1000);

                    // Map에서 조회 (없으면 기본값)
                    String imageUrl = imageMap.get(place.getId());
                    Long reviewCount = reviewCountMap.getOrDefault(place.getId(), 0L);
                    Double rating = ratingMap.getOrDefault(place.getId(), 0.0);
                    Long placeLikeCount = placeLikeCountMap.getOrDefault(place.getId(), 0L);
                    boolean isLiked = myLikedPlaceIds.contains(place.getId());

                    return PlaceMapResponse.of(place, distanceM, imageUrl, reviewCount, rating, placeLikeCount, isLiked);
                })
                .sorted(getComparator(sort)) // 정렬
                .collect(Collectors.toList());
    }

    @Transactional
    public void togglePlaceLike(Long placeId, Long userId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new CampEatException(CampEatErrorCode.PLACE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CampEatException(CampEatErrorCode.USER_NOT_FOUND));

        // 있으면 삭제(취소), 없으면 저장(찜)
        placeLikeRepository.findByPlaceAndUser(place, user)
                .ifPresentOrElse(
                        placeLikeRepository::delete,
                        () -> placeLikeRepository.save(PlaceLike.builder()
                                .place(place)
                                .user(user)
                                .build())
                );
    }

    private Comparator<PlaceMapResponse> getComparator(String sortType) {
        return switch (sortType.toUpperCase()) {
            // [평점순] 평점이 높은 순 -> 리뷰가 많은 순
            case "RATING", "LIKES" -> Comparator.comparing(PlaceMapResponse::rating).reversed()
                    .thenComparing(Comparator.comparing(PlaceMapResponse::reviewCount).reversed());

            // [리뷰순] 리뷰가 많은 순 -> 평점이 높은 순
            case "REVIEW" -> Comparator.comparing(PlaceMapResponse::reviewCount).reversed()
                    .thenComparing(Comparator.comparing(PlaceMapResponse::rating).reversed());

            // [기본] 거리 가까운 순 (DISTANCE, LATEST 등)
            default -> Comparator.comparing(PlaceMapResponse::distance);
        };
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
  
    
    public List<Place> parsePlacesFromJsonFile(String filePath) {
        List<Place> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray root = new JSONArray(sb.toString());

            for (int i = 0; i < root.length(); i++) {
                JSONObject obj = root.getJSONObject(i);
                JSONArray places = obj.getJSONArray("places");

                for (int j = 0; j < places.length(); j++) {
                    JSONObject p = places.getJSONObject(j);

                    Place place = Place.builder()
                            .id(Long.parseLong(p.getString("id")))
                            .placeName(p.optString("place_name", null))
                            .categoryGroupCode(p.optString("category_group_code", null))
                            .categoryGroupName(p.optString("category_group_name", null))
                            .categoryName(p.optString("category_name", null))
                            .phone(p.optString("phone", null))
                            .addressName(p.optString("address_name", null))
                            .roadAddressName(p.optString("road_address_name", null))
                            .x(p.has("x") ? p.getDouble("x") : null)
                            .y(p.has("y") ? p.getDouble("y") : null)
                            .distance(
                                    p.has("distance")
                                            ? Integer.parseInt(p.optString("distance", "0"))
                                            : 0
                            )
                            .placeUrl(p.optString("place_url", null))
                            .build();

                    result.add(place);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("places JSON 파싱 실패", e);
        }
        return result;
    }


    public void importPlacesFromJson(String filePath) {
        List<Place> places = parsePlacesFromJsonFile(filePath);
        placeRepository.saveAll(places);
    }


    @Transactional
    public void processPlace(Long id) {
        Place place = placeRepository.findByIdWithJpa(id);

        String prompt = String.format(
                "\"%s\"과 \"%s\"을 보고 생각하는 특징 단어로 10개 뽑아서 띄워쓰기로 구분하는 문장 만들어줘",
                place.getPlaceName(), place.getCategoryName()
        );

        String keywords = geminiApiService.getKeywordsFromGemini(prompt);
        float[] embedding = geminiApiService.getEmbeddingFromGemini(keywords);

        log.info("embedding length={}", embedding.length);
        qdrantService.createCollectionIfNotExists("restaurants",3072);
        qdrantService.saveEmbeddingToQdrant(place.getId(), embedding, keywords);
    }

    @Transactional
    public void processAllPlaces() {
        List<Place> places = placeRepository.findAll();

        for (Place place : places) {
            // placeId 기준으로 기존 로직 재사용
            processPlace(place.getId());
        }
    }
}
