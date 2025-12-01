package com.cloudproject.TeamC.CampEat.presentation;


import com.cloudproject.TeamC.CampEat.application.KakaoAPIService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao")
public class KakaoAPIController {

    private final KakaoAPIService kakaoAPIService;


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



}
