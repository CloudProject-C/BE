package com.cloudproject.TeamC.CampEat.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoAPIService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private static final String KAKAO_CATEGORY_SEARCH_URL =
            "https://dapi.kakao.com/v2/local/search/category.json";

    public JSONArray searchPlaces(double lat, double lng, int radius, String categoryGroupCode) {
        int targetCount = 100;     // 최대 몇 개까지 모을지
        int page = 1;
        int size = 15;             // 카테고리 검색 최대값
        JSONArray allDocs = new JSONArray();

        while (allDocs.length() < targetCount) {
            try {
                String urlStr = String.format(
                        "%s?category_group_code=%s&x=%f&y=%f&radius=%d&page=%d&size=%d",
                        KAKAO_CATEGORY_SEARCH_URL,
                        URLEncoder.encode(categoryGroupCode, "UTF-8"),
                        lng,
                        lat,
                        radius,
                        page,
                        size
                );

                log.info("Kakao API Request URL = {}", urlStr);

                HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "KakaoAK " + kakaoApiKey);

                int responseCode = conn.getResponseCode();
                log.info("Kakao API Response Code = {}", responseCode);

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                responseCode == HttpURLConnection.HTTP_OK
                                        ? conn.getInputStream()
                                        : conn.getErrorStream(),
                                "UTF-8"
                        )
                );

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                conn.disconnect();

                String responseBody = sb.toString();
                log.debug("Kakao API Response Body (page={}) = {}", page, responseBody);

                JSONObject json = new JSONObject(responseBody);

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    String msg = json.optString("message", "Kakao Local API error");
                    String type = json.optString("errorType", "UnknownError");
                    throw new RuntimeException("Kakao API Error [" + type + "] " + msg);
                }

                if (!json.has("documents") || !json.has("meta")) {
                    log.warn("Kakao API did not return documents/meta. full response = {}", json);
                    break; // 더 할 수 있는 게 없음
                }

                JSONArray docs = json.getJSONArray("documents");
                JSONObject meta = json.getJSONObject("meta");

                // 이번 페이지 결과들을 누적
                for (int i = 0; i < docs.length(); i++) {
                    allDocs.put(docs.getJSONObject(i));
                    if (allDocs.length() >= targetCount) {
                        break;
                    }
                }

                boolean isEnd = meta.optBoolean("is_end", true);
                int totalCount = meta.optInt("total_count", 0);
                log.info("page={} 수신 {}개, total_count={}, is_end={}",
                        page, docs.length(), totalCount, isEnd);

                // 마지막 페이지이거나, 결과가 더 없으면 중단
                if (isEnd || docs.length() == 0) {
                    break;
                }

                page++; // 다음 페이지로

            } catch (Exception e) {
                log.error("Failed calling Kakao API (page={})", page, e);
                throw new RuntimeException("Kakao API 호출 중 오류 발생", e);
            }
        }

        log.info("총 수신 개수 = {}", allDocs.length());
        return allDocs;
    }


    public void saveJsonToFile(JSONArray data, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(data.toString(4)); // pretty print
            log.info("JSON saved to file: {}", filename);
        } catch (IOException e) {
            log.error("Failed to write JSON to file {}: {}", filename, e.getMessage(), e);
            throw new RuntimeException("Failed to save JSON to file", e);
        }
    }
}
