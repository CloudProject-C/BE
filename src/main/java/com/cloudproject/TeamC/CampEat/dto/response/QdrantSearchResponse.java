package com.cloudproject.TeamC.CampEat.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class QdrantSearchResponse {
    private List<Result> result;

    @Getter
    @Setter
    public static class Result {
        private long id;
        private double score;
        private Map<String, Object> payload;
    }
}