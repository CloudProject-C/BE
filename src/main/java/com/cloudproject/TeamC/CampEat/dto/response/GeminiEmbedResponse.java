package com.cloudproject.TeamC.CampEat.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiEmbedResponse {
    private Embedding embedding;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Embedding {
        private float[] values;  // ✅ 이게 매번 새로운 배열인지 확인
    }
}