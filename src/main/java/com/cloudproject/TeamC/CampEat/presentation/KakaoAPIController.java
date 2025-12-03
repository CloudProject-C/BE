package com.cloudproject.TeamC.CampEat.presentation;


import com.cloudproject.TeamC.CampEat.application.KakaoAPIService;
import com.cloudproject.TeamC.CampEat.application.PlaceService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao")
public class KakaoAPIController {

    private final KakaoAPIService kakaoAPIService;
    private final PlaceService placeService;


    @GetMapping("/info")
    public String showMyInfo() {
        double lat = 37.247305;
        double lng = 127.078412;
        int radius = 500;


        JSONArray allResults = new JSONArray();

        // 음식점(FD6)
        JSONArray foodResults = kakaoAPIService.searchPlaces(lat, lng, radius, "FD6");
        allResults.put(new JSONObject()
                .put("category", "FD6")
                .put("places", foodResults));

        // 카페(CE7)
        JSONArray cafeResults = kakaoAPIService.searchPlaces(lat, lng, radius, "CE7");
        allResults.put(new JSONObject()
                .put("category", "CE7")
                .put("places", cafeResults));

        // JSON 파일로 저장
        kakaoAPIService.saveJsonToFile(allResults, "kakao_places.json");

        return "JSON 저장 완료: kakao_places.json";
    }

    @GetMapping("/change/{userId}")
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
    public ResponseEntity<Void> embedAll(
            @RequestParam(defaultValue = "20") int batchSize,
            @RequestParam(defaultValue = "1000") long delayMillis
    ) {
        placeService.processAllPlacesInBatches(batchSize, delayMillis);
        return ResponseEntity.ok().build();
    }


}
