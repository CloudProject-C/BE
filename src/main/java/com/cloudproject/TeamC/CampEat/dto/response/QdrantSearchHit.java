package com.cloudproject.TeamC.CampEat.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class QdrantSearchHit {
    private long id;
    private double score; // 0~1
    private Map<String, Object> payload;
}