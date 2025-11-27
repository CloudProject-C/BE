package com.cloudproject.TeamC.CampEat.presentation;

import com.cloudproject.TeamC.CampEat.dto.response.PlaceDetailResponse;
import com.cloudproject.TeamC.CampEat.application.PlaceService;
import com.cloudproject.TeamC.CampEat.presentation.swagger.PlaceSwagger;
import com.cloudproject.TeamC.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam Double longitude
    ) {
        PlaceDetailResponse response = placeService.getPlaceDetail(placeId, latitude, longitude);
        return CommonResponse.success(FETCH_PLACE_SUCCESS, response);
    }
}