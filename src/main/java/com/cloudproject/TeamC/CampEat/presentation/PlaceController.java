package com.cloudproject.TeamC.CampEat.presentation;

import com.cloudproject.TeamC.CampEat.domain.User;
import org.springframework.http.ResponseEntity;
import com.cloudproject.TeamC.CampEat.domain.FoodCategory;
import com.cloudproject.TeamC.CampEat.dto.response.PlaceDetailResponse;
import com.cloudproject.TeamC.CampEat.application.PlaceService;
import com.cloudproject.TeamC.CampEat.dto.response.PlaceMapResponse;
import com.cloudproject.TeamC.CampEat.presentation.swagger.PlaceSwagger;
import com.cloudproject.TeamC.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal User user
    ) {
        Long userId = user.getId();
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
            @AuthenticationPrincipal User user
    ) {
        Long userId = user.getId();
        List<PlaceMapResponse> response = placeService.getPlacesNearby(userId, latitude, longitude, radius, sort, category);
        return CommonResponse.success(FETCH_NEARBY_PLACES_SUCCESS, response);
    }

    @Override
    @PostMapping("/{placeId}/like")
    public CommonResponse<Void> togglePlaceLike(
            @AuthenticationPrincipal User user,
            @PathVariable Long placeId
    ) {
        Long userId = user.getId();
        placeService.togglePlaceLike(placeId, userId);
        return CommonResponse.success(PLACE_LIKE_TOGGLE_SUCCESS);
    }

}
