package com.cloudproject.TeamC.CampEat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Schema(description = "온보딩(취향 분석) 요청")
public class PreferenceRequest {
    @Schema(description = "선호하는 특징 키워드", example = "[\"매콤한\", \"한식\", \"디저트\", \"아이스크림\", \"빵\"]")
    private List<String> features;
}