package com.cloudproject.TeamC.CampEat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendationResponse {

    private Long id;
    private double score;
}