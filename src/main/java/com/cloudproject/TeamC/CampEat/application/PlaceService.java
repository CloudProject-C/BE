package com.cloudproject.TeamC.CampEat.application;


import com.cloudproject.TeamC.CampEat.domain.Place;
import com.cloudproject.TeamC.CampEat.infrastructure.persistence.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final GeminiApiService geminiApiService;
    private final QdrantService qdrantService;

    public List<Place> parsePlacesFromJsonFile(String filePath) {
        List<Place> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            // 최상위는 배열: [ { "places": [ ... ] } ]
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


//    public List<Place> parsePlacesFromJsonFile(String filePath) {
//        List<Place> result = new ArrayList<>();
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//            StringBuilder sb = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//            }
//
//            // 최상위는 배열: [ { "places": [ ... ] } ]
//            JSONArray root = new JSONArray(sb.toString());
//
//            for (int i = 0; i < root.length(); i++) {
//                JSONObject obj = root.getJSONObject(i);
//                JSONArray places = obj.getJSONArray("places");
//
//                for (int j = 0; j < places.length(); j++) {
//                    JSONObject p = places.getJSONObject(j);
//                    Place place = new Place();
//
//                    place.setId(Long.parseLong(p.getString("id")));
//                    place.setPlaceName(p.optString("place_name", null));
//                    place.setCategoryGroupCode(p.optString("category_group_code", null));
//                    place.setCategoryGroupName(p.optString("category_group_name", null));
//                    place.setCategoryName(p.optString("category_name", null));
//                    place.setPhone(p.optString("phone", null));
//                    place.setAddressName(p.optString("address_name", null));
//                    place.setRoadAddressName(p.optString("road_address_name", null));
//                    place.setX(p.optDouble("x"));
//                    place.setY(p.optDouble("y"));
//
//                    // distance는 문자열이므로 int로 변환 (없으면 0)
//                    String distanceStr = p.optString("distance", "0");
//                    place.setDistance(distanceStr.isEmpty() ? 0 : Integer.parseInt(distanceStr));
//
//                    place.setPlaceUrl(p.optString("place_url", null));
//
//                    result.add(place);
//                }
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException("places JSON 파싱 실패", e);
//        }
//
//        return result;
//    }

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
        qdrantService.createCollectionIfNotExists(768);
        qdrantService.saveEmbeddingToQdrant(place.getId(), embedding, keywords);
    }


}
