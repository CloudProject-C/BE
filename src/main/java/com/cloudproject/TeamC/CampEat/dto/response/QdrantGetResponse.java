package com.cloudproject.TeamC.CampEat.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QdrantGetResponse {
    private List<Point> result;

    @Getter
    @Setter
    public static class Point {
        private long id;
        private List<Double> vector;
    }
}