package com.cloudproject.TeamC.CampEat.presentation;

import org.springframework.http.ResponseEntity;
import com.cloudproject.TeamC.CampEat.domain.FoodCategory;
import com.cloudproject.TeamC.CampEat.dto.response.PlaceDetailResponse;
import com.cloudproject.TeamC.CampEat.application.PlaceService;
import com.cloudproject.TeamC.CampEat.dto.response.PlaceMapResponse;
import com.cloudproject.TeamC.CampEat.presentation.swagger.PlaceSwagger;
import com.cloudproject.TeamC.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cloudproject.TeamC.global.common.code.SuccessCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/places")
public class PlaceController implements PlaceSwagger {

    private final PlaceService placeService;

    @Override
    @GetMapping("/{placeId}")
    public CommonResponse<PlaceDetailResponse> getPlaceDetail(
            @PathVariable Long placeId,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) Long userId
    ) {
        PlaceDetailResponse response = placeService.getPlaceDetail(placeId, latitude, longitude, userId);
        return CommonResponse.success(FETCH_PLACE_SUCCESS, response);
    }

    @Override
    @GetMapping("/map")
    public CommonResponse<List<PlaceMapResponse>> getPlacesNearby(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "150") Double radius,
            @RequestParam(defaultValue = "DISTANCE") String sort,
            @RequestParam(required = false) FoodCategory category,
            @RequestParam(required = false) Long userId
    ) {
        List<PlaceMapResponse> response = placeService.getPlacesNearby(userId, latitude, longitude, radius, sort, category);
        return CommonResponse.success(FETCH_NEARBY_PLACES_SUCCESS, response);
    }

    @Override
    @PostMapping("/{placeId}/like")
    public CommonResponse<Void> togglePlaceLike(
            @PathVariable Long placeId,
            @RequestParam Long userId
    ) {
        // TODO: 로그인 생기면 userId 다시
        placeService.togglePlaceLike(placeId, userId);
        return CommonResponse.success(PLACE_LIKE_TOGGLE_SUCCESS);
    }
  
   @GetMapping("/change")
    public String change(@PathVariable Long userId){
        placeService.importPlacesFromJson("kakao_places.json",userId);
        return "db에 저장완료";
    }

    @PostMapping("/{id}/embed")
    public ResponseEntity<Void> embed(@PathVariable Long id) {
        placeService.processPlace(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/embed-all")
    public ResponseEntity<Void> embedAll() {
        placeService.processAllPlaces();
        return ResponseEntity.ok().build();
    }
}
