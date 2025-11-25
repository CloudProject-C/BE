package com.cloudproject.TeamC.CampEat.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeminiEmbedResponse {
    private Embedding embedding;

    @Getter
    @Setter
    public static class Embedding {
        private float[] values;
    }
}
