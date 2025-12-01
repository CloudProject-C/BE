package com.cloudproject.TeamC.CampEat.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class QdrantScrollResponse {

    private Result result;
    @Getter @Setter
    public static class Result {
        private List<Point> points;
    }

    @Getter @Setter
    public static class Point {
        private long id;
        private List<Double> vector;
        private Map<String, Object> payload;
    }
}