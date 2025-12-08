package com.cloudproject.TeamC.CampEat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "매일 선호도 조사")
public class RecommendRequest {
    @Schema(description = "선호하는 특징 키워드", example = "[\"매콤한\", \"한식\", \"디저트\", \"아이스크림\", \"빵\"]")
    private List<String> features; // 새로 받은 10개 특징
}